package com.nfeld.jsonpathkt.kotlinx

import com.nfeld.jsonpathkt.JsonPath
import com.nfeld.jsonpathkt.resolve
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

public fun JsonElement.resolvePath(path: String): JsonElement? = JsonPath.compile(path).resolve(this)
public fun JsonElement.resolve(jsonPath: JsonPath): JsonElement? = jsonPath.resolve(this)

public fun JsonElement.resolveAsStringOrNull(path: String): String? =
  when (val value = resolvePath(path)) {
    is JsonPrimitive -> value.contentOrNull
    else -> null
  }

public fun JsonElement.resolveAsStringOrNull(path: JsonPath): String? =
  when (val value = resolve(path)) {
    is JsonPrimitive -> value.contentOrNull
    else -> null
  }

public fun JsonPath.resolve(json: JsonElement): JsonElement? {
  if (json is JsonNull) return JsonNull

  return resolve<JsonElement>(
    KotlinxJsonNode(json, isWildcardScope = false),
  )
}
