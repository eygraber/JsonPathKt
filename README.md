# JsonPathKt
[![Build Status](https://travis-ci.com/codeniko/JsonPathKt.svg?branch=master)](https://travis-ci.com/codeniko/JsonPathKt)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.eygraber/jsonpathkt/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.eygraber/jsonpathkt)
[![codecov](https://codecov.io/gh/codeniko/JsonPathKt/branch/master/graph/badge.svg)](https://codecov.io/gh/codeniko/JsonPathKt)

**A lighter and more efficient implementation of JsonPath in Kotlin.**
With functional programming aspects found in langauges like Kotlin, Scala, and streams/lambdas in Java8, this
library simplifies other implementations like [Jayway's JsonPath](https://github.com/json-path/JsonPath) by removing 
*filter operations* and *in-path functions* to focus on what matters most: modern fast value extractions from JSON objects. 
Up to **4x more efficient** in some cases; see [Benchmarks](#benchmarks).

In order to make the library functional programming friendly, JsonPathKt returns `null` instead of throwing exceptions 
while evaluating a path against a JSON object. Throwing exceptions breaks flow control and should be reserved for exceptional 
errors only.

## Code examples
A jsonpath that exists returns that value. `null` is returned when it doesn't.
```kotlin
val json = """{"hello": "world"}"""
JsonPath.parse(json)?.read<String>("$.hello") // returns "world"
JsonPath.parse(json)?.read<String>("$.somethingelse") // returns null since "somethingelse" key not found
```

A jsonpath that returns a collection containing the 2nd and 3rd items in the list (index 0 based and exclusive at range end).
```kotlin
val json = """{"list": ["a","b","c","d"]}"""
JsonPath.parse(json)?.read<List<String>>("$.list[1:3]") // returns listOf("b", "c")
```

JsonPathKt also works with `Map` and POJO.
```kotlin
val json = """[{ "outer": {"inner": 1} }]"""
JsonPath.parse(json)?.read<Map<String, Int>>("$[0].outer") // returns mapOf("inner" to 1)
data class ParsedResult(val outer: Map<String, Int>) // define this class in file scope, not in function scope which will anonymize it 
JsonPath.parse(json)?.read<ParsedResult>("$[0]") // returns ParsedResult instance
```

Internally, a jsonpath is compiled into a list of tokens. You can compile a complex jsonpath once and reuse it across multiple JSON strings.
```kotlin
val jsonpath = JsonPath("$.family.children..['name','nickname']")
jsonpath.readFromJson<List<Map<String, String>>>(json1)
jsonpath.readFromJson<List<Map<String, String>>>(json2)
```

*JsonPathKt uses [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) to deserialize JSON strings. `JsonPath.parse` returns a 
`JsonElement` object, so if you've already deserialized, you can also `read` the jsonpath value directly.*


## Getting started
JsonPathKt is available at the Maven Central repository.

**POM**
```xml
<dependency>
  <groupId>com.nfeld.jsonpathkt</groupId>
  <artifactId>jsonpathkt</artifactId>
  <version>2.0.1</version>
</dependency>
```

**Gradle**
```gradle
dependencies {
    implementation 'com.nfeld.jsonpathkt:jsonpathkt:2.0.1'
}
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
These are benchmark tests of JsonPathKt against Jayway's JsonPath implementation. Results for each test is the average of 
30 runs with 80,000 reads per run and each test returns its own respective results (some larger than others).
You can run these tests locally with `./runBenchmarks.sh`

**Evaluating/reading path against large JSON**

| Path Tested                             | JsonPathKt (ms) | JsonPath (ms) |
|:----------------------------------------|:----------------|:--------------|
| $[0].friends[1].other.a.b['c']          | 31 ms           | 81 ms         |
| $[2]._id                                | 11 ms           | 28 ms         |
| $..name                                 | 42 ms           | 472 ms        |
| $..['email','name']                     | 55 ms           | 479 ms        |
| $..[1]                                  | 40 ms           | 412 ms        |
| $..[:2]                                 | 47 ms           | 462 ms        |
| $..[2:]                                 | 69 ms           | 443 ms        |
| $[0]['tags'][-3]                        | 22 ms           | 45 ms         |
| $[0]['tags'][:3]                        | 29 ms           | 62 ms         |
| $[0]['tags'][3:]                        | 30 ms           | 68 ms         |
| $[0]['tags'][3:5]                       | 30 ms           | 62 ms         |
| $[0]['tags'][0,3,5]                     | 26 ms           | 63 ms         |
| $[0]['latitude','longitude','isActive'] | 31 ms           | 96 ms         |
| $[0]['tags'].*                          | 17 ms           | 73 ms         |
| $[0]..*                                 | 85 ms           | 597 ms        |


**Compiling JsonPath strings to internal tokens**

| Path size           | JsonPathKt | JsonPath |
|:--------------------|:-----------|:---------|
| 7 chars, 1 tokens   | 5 ms       | 5 ms     |
| 16 chars, 3 tokens  | 11 ms      | 13 ms    |
| 30 chars, 7 tokens  | 21 ms      | 32 ms    |
| 65 chars, 16 tokens | 48 ms      | 69 ms    |
| 88 chars, 19 tokens | 66 ms      | 103 ms   |

# Cache
JsonPathKt doesn't provide a caching layer anymore. If caching is desired, there are multiple
KMP caching libraries that can be used to wrap JsonPathKt.
```

[![Analytics](https://ga-beacon.appspot.com/UA-116910991-3/jsonpathlite/index)](https://github.com/igrigorik/ga-beacon)
