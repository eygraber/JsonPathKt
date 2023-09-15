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

It targets JVM, JS (node and browser), and all native targets (except Android native).

```gradle
dependencies {
    implementation("com.nfeld.jsonpathkt:jsonpathkt-kotlinx:2.0.0")
}
```

### JSON-java (org.json)
You can use JsonPathKt with [JSON-java (org.json)](https://github.com/stleary/JSON-java) in your JVM projects.

```gradle
dependencies {
    implementation("com.nfeld.jsonpathkt:jsonpathkt-jsonjava:2.0.0")
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
| $[0].friends[1].other.a.b['c']           |          26 ms       |         294 ms       |          53 ms       |
| $[2]._id                                 |           7 ms       |         270 ms       |          18 ms       |
| $..name                                  |          38 ms       |          68 ms       |         266 ms       |
| $..['email','name']                      |          51 ms       |          71 ms       |         276 ms       |
| $..[1]                                   |          35 ms       |         182 ms       |         261 ms       |
| $..[:2]                                  |          42 ms       |         291 ms       |         274 ms       |
| $..[2:]                                  |          61 ms       |         700 ms       |         282 ms       |
| $..[1:-1]                                |          61 ms       |         771 ms       |         248 ms       |
| $[0]['tags'][-3]                         |          13 ms       |         294 ms       |          33 ms       |
| $[0]['tags'][:3]                         |          20 ms       |         302 ms       |          41 ms       |
| $[0]['tags'][3:]                         |          21 ms       |         292 ms       |          45 ms       |
| $[0]['tags'][3:5]                        |          21 ms       |         298 ms       |          40 ms       |
| $[0]['tags'][0,3,5]                      |          21 ms       |         302 ms       |          50 ms       |
| $[0]['latitude','longitude','isActive']  |          23 ms       |         303 ms       |          70 ms       |
| $[0]['tags'].*                           |          12 ms       |         291 ms       |          52 ms       |
| $[0]..*                                  |          67 ms       |         289 ms       |         455 ms       |

**LINUX_X64**
| Path Tested                              |  JsonPathKt  |
|:-----------------------------------------|:-------------|
| $[0].friends[1].other.a.b['c']           |     145 ms   |
| $[2]._id                                 |      59 ms   |
| $..name                                  |     249 ms   |
| $..['email','name']                      |     294 ms   |
| $..[1]                                   |     206 ms   |
| $..[:2]                                  |     221 ms   |
| $..[2:]                                  |     355 ms   |
| $..[1:-1]                                |     345 ms   |
| $[0]['tags'][-3]                         |      97 ms   |
| $[0]['tags'][:3]                         |     134 ms   |
| $[0]['tags'][3:]                         |     144 ms   |
| $[0]['tags'][3:5]                        |     135 ms   |
| $[0]['tags'][0,3,5]                      |     139 ms   |
| $[0]['latitude','longitude','isActive']  |     130 ms   |
| $[0]['tags'].*                           |      78 ms   |
| $[0]..*                                  |     564 ms   |

**JS Node**
| Path Tested                              |  JsonPathKt  |
|:-----------------------------------------|:-------------|
| $[0].friends[1].other.a.b['c']           |     169 ms   |
| $[2]._id                                 |      54 ms   |
| $..name                                  |     191 ms   |
| $..['email','name']                      |     250 ms   |
| $..[1]                                   |     156 ms   |
| $..[:2]                                  |     174 ms   |
| $..[2:]                                  |     223 ms   |
| $..[1:-1]                                |     228 ms   |
| $[0]['tags'][-3]                         |      87 ms   |
| $[0]['tags'][:3]                         |     111 ms   |
| $[0]['tags'][3:]                         |     117 ms   |
| $[0]['tags'][3:5]                        |     111 ms   |
| $[0]['tags'][0,3,5]                      |     118 ms   |
| $[0]['latitude','longitude','isActive']  |     199 ms   |
| $[0]['tags'].*                           |      69 ms   |
| $[0]..*                                  |     304 ms   |

**Compiling JsonPath strings to internal tokens**

**JVM**
| Path Size                                |  JsonPathKt  |   JsonPath   |
|:-----------------------------------------|:-------------|:-------------|
| 7 chars, 1 tokens                        |       3 ms   |       2 ms   |
| 16 chars, 3 tokens                       |       8 ms   |       7 ms   |
| 30 chars, 7 tokens                       |      15 ms   |      19 ms   |
| 65 chars, 16 tokens                      |      34 ms   |      47 ms   |
| 88 chars, 19 tokens                      |      44 ms   |      70 ms   |


**LINUX_64**
| Path Size                                |  JsonPathKt  |
|:-----------------------------------------|:-------------|
| 7 chars, 1 tokens                        |      31 ms   |
| 16 chars, 3 tokens                       |      59 ms   |
| 30 chars, 7 tokens                       |     117 ms   |
| 65 chars, 16 tokens                      |     258 ms   |
| 88 chars, 19 tokens                      |     365 ms   |

**JS Node**
| Path Size                                |  JsonPathKt  |
|:-----------------------------------------|:-------------|
| 7 chars, 1 tokens                        |      35 ms   |
| 16 chars, 3 tokens                       |      74 ms   |
| 30 chars, 7 tokens                       |     126 ms   |
| 65 chars, 16 tokens                      |     272 ms   |
| 88 chars, 19 tokens                      |     337 ms   |
