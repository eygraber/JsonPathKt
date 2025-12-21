package com.nfeld.jsonpathkt.kotlinx

import com.nfeld.jsonpathkt.JsonPath
import com.nfeld.jsonpathkt.ResolutionOptions
import com.nfeld.jsonpathkt.resolveOrNull
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

public fun JsonElement.resolvePathOrNull(
  path: String,
  options: ResolutionOptions = ResolutionOptions.Default,
): JsonElement? =
  JsonPath.compile(path).resolveOrNull(this, options)

public fun JsonElement.resolveOrNull(
  path: JsonPath,
  options: ResolutionOptions = ResolutionOptions.Default,
): JsonElement? = path.resolveOrNull(this, options)

public fun JsonElement.resolvePathAsStringOrNull(path: String): String? =
  JsonPath.compile(path).resolveAsStringOrNull(this)

public fun JsonElement.resolveAsStringOrNull(path: JsonPath): String? =
  path.resolveAsStringOrNull(this)

public fun JsonPath.resolveOrNull(
  json: JsonElement,
  options: ResolutionOptions = ResolutionOptions.Default,
): JsonElement? {
  if (json is JsonNull) return null

  return resolveOrNull<JsonElement>(
    KotlinxJsonNode(json, isWildcardScope = false),
    options,
  )
}

public fun JsonPath.resolveAsStringOrNull(json: JsonElement): String? {
  if (json is JsonNull) return null

  val value = resolveOrNull<JsonElement>(
    KotlinxJsonNode(json, isWildcardScope = false),
  )

  return when (value) {
    is JsonPrimitive -> value.contentOrNull
    is JsonArray,
    is JsonObject,
    null,
    -> null
  }
}
