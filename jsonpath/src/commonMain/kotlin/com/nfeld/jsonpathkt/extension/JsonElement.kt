@file:Suppress("NOTHING_TO_INLINE")

package com.nfeld.jsonpathkt.extension

import com.nfeld.jsonpathkt.JsonPath
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.decodeFromJsonElement

inline fun <reified T : Any> JsonElement.read(jsonpath: String): T? = try {
  JsonPath(jsonpath).read(this)?.let { Json.decodeFromJsonElement(it) }
} catch (_: Throwable) {
  null
}

inline fun <reified T : Any> JsonElement.read(jsonpath: JsonPath): T? = try {
  jsonpath.read(this)?.let { Json.decodeFromJsonElement(it) }
} catch (_: Throwable) {
  null
}

internal inline fun JsonElement?.isNotNullOrMissing() =
  this != null && this !is JsonNull
