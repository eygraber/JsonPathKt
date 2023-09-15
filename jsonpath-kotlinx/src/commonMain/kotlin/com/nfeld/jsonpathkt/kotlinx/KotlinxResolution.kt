package com.nfeld.jsonpathkt.kotlinx

import com.nfeld.jsonpathkt.JsonPath
import com.nfeld.jsonpathkt.resolveOrNull
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

public fun JsonElement.resolvePathOrNull(path: String): JsonElement? = JsonPath.compile(path).resolveOrNull(this)
public fun JsonElement.resolveOrNull(path: JsonPath): JsonElement? = path.resolveOrNull(this)

public fun JsonElement.resolvePathAsStringOrNull(path: String): String? =
  JsonPath.compile(path).resolveAsStringOrNull(this)

public fun JsonElement.resolveAsStringOrNull(path: JsonPath): String? =
  path.resolveAsStringOrNull(this)

public fun JsonPath.resolveOrNull(json: JsonElement): JsonElement? {
  if (json is JsonNull) return null

  return resolveOrNull<JsonElement>(
    KotlinxJsonNode(json, isWildcardScope = false),
  )
}

public fun JsonPath.resolveAsStringOrNull(json: JsonElement): String? {
  if (json is JsonNull) return null

  val value = resolveOrNull<JsonElement>(
    KotlinxJsonNode(json, isWildcardScope = false),
  )

  return when (value) {
    is JsonPrimitive -> value.contentOrNull
    else -> null
  }
}
