package com.nfeld.jsonpathkt

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

internal fun printTesting(subpath: String) {
  println("Testing like $subpath")
}

internal fun emptyJsonObject() = JsonObject(emptyMap())
internal fun emptyJsonArray() = JsonArray(emptyList())
internal fun JsonElement.toJsonNode(isNewRoot: Boolean = false) = JsonNode(element = this, isNewRoot = isNewRoot)

fun readTree(json: String): JsonElement = Json.parseToJsonElement(json)
