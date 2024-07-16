# JsonPathKt
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.eygraber/jsonpathkt-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.eygraber/jsonpathkt-core)

**A lighter and more efficient implementation of JsonPath in Kotlin Multiplatform (KMP).**
With functional programming aspects found in languages like Kotlin, Scala, and streams/lambdas in Java8, this
library simplifies other implementations like [Jayway's JsonPath](https://github.com/json-path/JsonPath) by removing 
*filter operations* and *in-path functions* to focus on what matters most: modern fast value extractions from JSON objects. 
Up to **7x more efficient** in some cases; see [Benchmarks](#benchmarks).

In order to make the library functional programming friendly, JsonPathKt returns `null` instead of throwing exceptions 
while evaluating a path against a JSON object. Throwing exceptions breaks flow control and should be reserved for exceptional 
errors only.

## Credit
Ported from @codeniko [JsonPathKt](https://github.com/codeniko/JsonPathKt)

## Getting started
JsonPathKt is available at the Maven Central repository.

### Kotlinx Serialization
You can use JsonPathKt with [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) in your KMP projects.

It targets JVM, JS (node and browser), and all native targets.

```gradle
dependencies {
    implementation("com.eygraber:jsonpathkt-kotlinx:3.0.2")
}
```

### JSON-java (org.json)
You can use JsonPathKt with [JSON-java (org.json)](https://github.com/stleary/JSON-java) in your JVM projects.

```gradle
dependencies {
    implementation("com.eygraber:jsonpathkt-jsonjava:3.0.2")
}
```

## Code examples
Internally, a jsonpath is compiled into a list of tokens. You can compile a complex jsonpath once and reuse it across multiple JSON strings.
```kotlin
val jsonpath = JsonPath.compile("$.family.children..['name','nickname']")
```

`JsonPath.resolveOrNull` will return your implementation's native JSON type. `JsonPath.resolveAsStringOrNull` will return a `String` if that is what is resolved, otherwise it will return `null`.

```kotlin
jsonpath.resolveOrNull(json1)
jsonpath.resolveAsStringOrNull(json2)
```

Each implementation provides extension functions on its JSON types to allow for easy resolution. Using Kotlinx Serialization as an example:

```kotlin
val json = Json.parseToJsonElement("""{"hello": "world"}""")
val helloPath = JsonPath.compile("$.hello")
val somethingElsePath = JsonPath.compile("$.somethingelse")

json?.resolveOrNull(helloPath) // returns JsonPrimitve("world")
json?.resolveAsStringOrNull(helloPath) // returns "world"

json?.resolvePathOrNull("$.hello") // returns JsonPrimitve("world")
json?.resolvePathAsStringOrNull("$.hello") // returns "world"

json?.resolveOrNull(somethingElsePath) // returns null since "somethingelse" key not found
json?.resolveAsStringOrNull(somethingElsePath) // returns null since "somethingelse" key not found

json?.resolvePathOrNull("$.somethingelse") // returns null since "somethingelse" key not found
json?.resolvePathAsStringOrNull("$.somethingelse") // returns null since "somethingelse" key not found
```

Another example; a jsonpath that returns a collection containing the 2nd and 3rd items in the list (index 0 based and exclusive at range end).
```kotlin
val json = Json.parseToJsonElement("""{"list": ["a","b","c","d"]}""")

json?.resolvePathOrNull("$.list[1:3]") // returns JsonArray(listOf("b", "c"))
json?.resolvePathAsStringOrNull("$.list[1:3]") // returns null since the result is not a String
```

If you want to resolve a JSON type as a `String`, you can use `resolvePathOrNull` and your implementation's JSON type to do that:
```kotlin
val json = Json.parseToJsonElement("""{"list": ["a","b","c","d"]}""")

json?.resolvePathOrNull("$.list[1:3]")?.toString // returns '["b", "c"]'
```

## Accessor operators

| Operator                  | Description                                                          |
|:--------------------------|:---------------------------------------------------------------------|
| `$`                       | The root element to query. This begins all path expressions.         |
| `..`                      | Deep scan for values behind followed key value accessor              |
| `.<name>`                 | Dot-notated key value accessor for JSON objects                      |
| `['<name>' (, '<name>')]` | Bracket-notated key value accessor for JSON objects, comma-delimited |
| `[<number> (, <number>)]` | JSON array accessor for index or comma-delimited indices             |
| `[start:end]`             | JSON array range accessor from start (inclusive) to end (exclusive)  |

## Path expression examples
JsonPathKt expressions can use any combination of dot–notation and bracket–notation operators to access JSON values. For examples, these all evaluate to the same result:
```text
$.family.children[0].name
$['family']['children'][0]['name']
$['family'].children[0].name
```

Given the JSON:
```json
{
    "family": {
        "children": [{
                "name": "Thomas",
                "age": 13
            },
            {
                "name": "Mila",
                "age": 18
            },
            {
                "name": "Konstantin",
                "age": 29,
                "nickname": "Kons"
            },
            {
                "name": "Tracy",
                "age": 4
            }
        ]
    }
}
```

| JsonPath                   | Result                                     |
|:---------------------------|:-------------------------------------------|
| $.family                   | The family object                          |
| $.family.children          | The children array                         |
| $.family['children']       | The children array                         |
| $.family.children[2]       | The second child object                    |
| $.family.children[-1]      | The last child object                      |
| $.family.children[-3]      | The 3rd to last child object               |
| $.family.children[1:3]     | The 2nd and 3rd children objects           |
| $.family.children[:3]      | The first three children                   |
| $.family.children[:-1]     | The first three children                   |
| $.family.children[2:]      | The last two children                      |
| $.family.children[-2:]     | The last two children                      |
| $..name                    | All names                                  |
| $.family..name             | All names nested within family object      |
| $.family.children[:3]..age | The ages of first three children           |
| $..['name','nickname']     | Names & nicknames (if any) of all children |
| $.family.children[0].*     | Names & age values of first child          |

## Benchmarks
These are benchmark tests of JsonPathKt against other implementations. Results for each test is the average of 
30 runs with 80,000 reads per run and each test returns its own respective results (some larger than others).

**Evaluating/reading path against large JSON**

**JVM**
| Path Tested                              |   JsonPathKtKotlinx  |  JsonPathKtJsonJava  |       JsonPath       |
|:-----------------------------------------|:---------------------|:---------------------|:---------------------|
| $[0].friends[1].other.a.b['c']           |          24 ms       |         248 ms       |          50 ms       |
| $[2]._id                                 |           6 ms       |         230 ms       |          17 ms       |
| $..name                                  |          37 ms       |          60 ms       |         263 ms       |
| $..['email','name']                      |          52 ms       |          62 ms       |         273 ms       |
| $..[1]                                   |          34 ms       |         154 ms       |         261 ms       |
| $..[:2]                                  |          43 ms       |         249 ms       |         267 ms       |
| $..[2:]                                  |          53 ms       |         607 ms       |         278 ms       |
| $..[1:-1]                                |          57 ms       |         680 ms       |         242 ms       |
| $[0]['tags'][-3]                         |          14 ms       |         251 ms       |          30 ms       |
| $[0]['tags'][:3]                         |          22 ms       |         258 ms       |          41 ms       |
| $[0]['tags'][3:]                         |          24 ms       |         254 ms       |          43 ms       |
| $[0]['tags'][3:5]                        |          22 ms       |         258 ms       |          38 ms       |
| $[0]['tags'][0,3,5]                      |          21 ms       |         257 ms       |          48 ms       |
| $[0]['latitude','longitude','isActive']  |          23 ms       |         262 ms       |          68 ms       |
| $[0]['tags'].*                           |          13 ms       |         248 ms       |          46 ms       |
| $[0]..*                                  |          59 ms       |         246 ms       |         451 ms       |

**LINUX_X64**
| Path Tested                              |  JsonPathKt  |
|:-----------------------------------------|:-------------|
| $[0].friends[1].other.a.b['c']           |     103 ms   |
| $[2]._id                                 |      38 ms   |
| $..name                                  |     181 ms   |
| $..['email','name']                      |     212 ms   |
| $..[1]                                   |     148 ms   |
| $..[:2]                                  |     155 ms   |
| $..[2:]                                  |     234 ms   |
| $..[1:-1]                                |     237 ms   |
| $[0]['tags'][-3]                         |      71 ms   |
| $[0]['tags'][:3]                         |      92 ms   |
| $[0]['tags'][3:]                         |      98 ms   |
| $[0]['tags'][3:5]                        |      90 ms   |
| $[0]['tags'][0,3,5]                      |      99 ms   |
| $[0]['latitude','longitude','isActive']  |      92 ms   |
| $[0]['tags'].*                           |      55 ms   |
| $[0]..*                                  |     339 ms   |

**JS Node**
| Path Tested                              |  JsonPathKt  |
|:-----------------------------------------|:-------------|
| $[0].friends[1].other.a.b['c']           |     101 ms   |
| $[2]._id                                 |      40 ms   |
| $..name                                  |     172 ms   |
| $..['email','name']                      |     211 ms   |
| $..[1]                                   |     139 ms   |
| $..[:2]                                  |     155 ms   |
| $..[2:]                                  |     192 ms   |
| $..[1:-1]                                |     192 ms   |
| $[0]['tags'][-3]                         |      70 ms   |
| $[0]['tags'][:3]                         |      90 ms   |
| $[0]['tags'][3:]                         |      95 ms   |
| $[0]['tags'][3:5]                        |      88 ms   |
| $[0]['tags'][0,3,5]                      |     106 ms   |
| $[0]['latitude','longitude','isActive']  |     103 ms   |
| $[0]['tags'].*                           |      61 ms   |
| $[0]..*                                  |     268 ms   |

**WasmJs**
| Path Tested                              |  JsonPathKt  |
|:-----------------------------------------|:-------------|
| $[0].friends[1].other.a.b['c']           |      78 ms   |
| $[2]._id                                 |      30 ms   |
| $..name                                  |     109 ms   |
| $..['email','name']                      |     138 ms   |
| $..[1]                                   |      87 ms   |
| $..[:2]                                  |      95 ms   |
| $..[2:]                                  |     148 ms   |
| $..[1:-1]                                |     147 ms   |
| $[0]['tags'][-3]                         |      58 ms   |
| $[0]['tags'][:3]                         |      74 ms   |
| $[0]['tags'][3:]                         |      81 ms   |
| $[0]['tags'][3:5]                        |      77 ms   |
| $[0]['tags'][0,3,5]                      |      92 ms   |
| $[0]['latitude','longitude','isActive']  |      84 ms   |
| $[0]['tags'].*                           |      47 ms   |
| $[0]..*                                  |     270 ms   |

**Compiling JsonPath strings to internal tokens**

**JVM**
| Path Size                                |  JsonPathKt  |   JsonPath   |
|:-----------------------------------------|:-------------|:-------------|
| 7 chars, 1 tokens                        |       2 ms   |       2 ms   |
| 16 chars, 3 tokens                       |       6 ms   |       8 ms   |
| 30 chars, 7 tokens                       |      13 ms   |      19 ms   |
| 65 chars, 16 tokens                      |      33 ms   |      47 ms   |
| 88 chars, 19 tokens                      |      44 ms   |      73 ms   |


**LINUX_X64**
| Path Size                                |  JsonPathKt  |
|:-----------------------------------------|:-------------|
| 7 chars, 1 tokens                        |      14 ms   |
| 16 chars, 3 tokens                       |      34 ms   |
| 30 chars, 7 tokens                       |      72 ms   |
| 65 chars, 16 tokens                      |     177 ms   |
| 88 chars, 19 tokens                      |     250 ms   |

**JS Node**
| Path Size                                |  JsonPathKt  |
|:-----------------------------------------|:-------------|
| 7 chars, 1 tokens                        |      13 ms   |
| 16 chars, 3 tokens                       |      37 ms   |
| 30 chars, 7 tokens                       |      83 ms   |
| 65 chars, 16 tokens                      |     189 ms   |
| 88 chars, 19 tokens                      |     254 ms   |

**WasmJs**
| Path Size                                |  JsonPathKt  |
|:-----------------------------------------|:-------------|
| 7 chars, 1 tokens                        |       9 ms   |
| 16 chars, 3 tokens                       |      26 ms   |
| 30 chars, 7 tokens                       |      64 ms   |
| 65 chars, 16 tokens                      |     154 ms   |
| 88 chars, 19 tokens                      |     221 ms   |
