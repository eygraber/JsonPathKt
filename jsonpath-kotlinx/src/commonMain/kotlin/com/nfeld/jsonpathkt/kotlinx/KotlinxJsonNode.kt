package com.nfeld.jsonpathkt.kotlinx

import com.nfeld.jsonpathkt.json.JsonNode
import com.nfeld.jsonpathkt.json.JsonType
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

private val defaultEmptyJsonArray = JsonArray(emptyList())

public fun JsonElement.toJsonNode(isWildcardScope: Boolean): JsonNode =
  KotlinxJsonNode(this, isWildcardScope)

internal class KotlinxJsonNode(
  element: Any,
  isWildcardScope: Boolean,
) : JsonNode(element, isWildcardScope) {
  override val type: JsonType = when (element as JsonElement) {
    is JsonArray -> JsonType.Array
    is JsonObject -> JsonType.Object
    is JsonPrimitive -> when (element) {
      is JsonNull -> JsonType.Null
      else -> JsonType.Primitive
    }
  }

  override val asArray: List<Any> get() = element as JsonArray

  override val asObject: Map<String, Any> get() = element as JsonObject

  override val asObjectValues: Collection<Any> get() = (element as JsonObject).values

  override val asString: String?
    get() = with(element as JsonPrimitive) {
      if (isString) contentOrNull else null
    }

  override val emptyJsonArray: Any get() = defaultEmptyJsonArray

  override val Any?.isNull: Boolean get() = this == null || this is JsonNull

  override val isNotNull get() = element !is JsonNull

  @Suppress("UNCHECKED_CAST")
  override fun toJsonArray(list: List<Any>): Any = JsonArray(list as List<JsonElement>)

  override fun createJsonLiteral(content: String): Any = JsonPrimitive(content)

  override fun copy(element: Any, isWildcardScope: Boolean) = KotlinxJsonNode(element, isWildcardScope)
}
