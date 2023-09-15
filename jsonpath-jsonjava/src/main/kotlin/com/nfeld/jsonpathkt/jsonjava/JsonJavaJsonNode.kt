package com.nfeld.jsonpathkt.jsonjava

import com.nfeld.jsonpathkt.json.JsonNode
import com.nfeld.jsonpathkt.json.JsonType
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONString

private val defaultEmptyJsonArray = JSONArray()

public fun JSONArray.toJsonNode(isWildcardScope: Boolean): JsonNode =
  OrgJsonNode(this, isWildcardScope)

public fun JSONObject.toJsonNode(isWildcardScope: Boolean): JsonNode =
  OrgJsonNode(this, isWildcardScope)

internal class OrgJsonNode(
  element: Any,
  isWildcardScope: Boolean,
) : JsonNode(element, isWildcardScope) {
  override val type: JsonType = when (element) {
    is JSONArray -> JsonType.Array
    is JSONObject -> JsonType.Object
    is JSONString -> JsonType.Primitive
    else -> JsonType.Null
  }

  override val asArray: List<Any> get() = (element as JSONArray).toList()

  override val asObject: Map<String, Any> get() = (element as JSONObject).toMap()

  override val asObjectValues: Collection<Any> get() = (element as JSONObject).toMap().values

  override val asString: String? get() = (element as JSONString).toJSONString()

  override val emptyJsonArray: Any = defaultEmptyJsonArray

  override val Any?.isNull: Boolean get() = this == null // || type == JsonType.Null

  override val isNotNull: Boolean get() = true // type != JsonType.Null

  override fun createJsonLiteral(content: String): Any = JSONString { content }

  override fun toJsonArray(list: List<Any>): Any = JSONArray(list)

  override fun copy(element: Any, isWildcardScope: Boolean): JsonNode =
    OrgJsonNode(element, isWildcardScope)
}
