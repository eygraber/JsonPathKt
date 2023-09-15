package com.nfeld.jsonpathkt

import com.nfeld.jsonpathkt.json.JsonNode
import com.nfeld.jsonpathkt.kotlinx.resolvePath
import com.nfeld.jsonpathkt.kotlinx.toJsonNode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

val LARGE_PARSED_JSON by lazy {
  Json.parseToJsonElement(LARGE_JSON)
}

internal fun printTesting(subpath: String) {
  println("Testing like $subpath")
}

internal fun emptyJsonObject() = JsonObject(emptyMap())
internal fun emptyJsonArray() = JsonArray(emptyList())

internal fun JsonElement.jsonNode(isWildcardScope: Boolean = false) =
  toJsonNode(isWildcardScope = isWildcardScope)

internal inline fun <reified T : Any> JsonElement.resolveAsType(
  path: String,
): T? = try {
  resolvePath(path)?.let { Json.decodeFromJsonElement(it) }
} catch (_: Throwable) {
  null
}

internal val JsonNode.asJson get() = element as JsonElement

internal val String.asJson: JsonElement
  get() = Json.parseToJsonElement(this)
