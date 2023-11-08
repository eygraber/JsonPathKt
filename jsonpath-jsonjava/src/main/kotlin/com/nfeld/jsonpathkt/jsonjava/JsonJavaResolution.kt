package com.nfeld.jsonpathkt.jsonjava

import com.nfeld.jsonpathkt.JsonPath
import com.nfeld.jsonpathkt.ResolutionOptions
import com.nfeld.jsonpathkt.resolveOrNull
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONString

@JvmInline
public value class JSONElement(@PublishedApi internal val element: Any) {
  public inline val isArray: Boolean get() = element is JSONArray
  public inline val asArray: JSONArray get() = element as JSONArray
  public inline val asArrayOrNull: JSONArray? get() = element as? JSONArray

  public inline val isObject: Boolean get() = element is JSONObject
  public inline val asObject: JSONObject get() = element as JSONObject
  public inline val asObjectOrNull: JSONObject? get() = element as? JSONObject

  public inline val isString: Boolean get() = element is JSONString
  public inline val asString: JSONString get() = element as JSONString
  public inline val asStringOrNull: JSONString? get() = element as? JSONString
}

public fun JSONArray.resolveOrNull(
  path: JsonPath,
  options: ResolutionOptions = ResolutionOptions.Default,
): JSONElement? = path.resolveOrNull(this, options)

public fun JSONArray.resolveAsStringOrNull(path: JsonPath): String? =
  path.resolveOrNull(this)?.asStringOrNull?.toJSONString()

public fun JSONArray.resolvePathOrNull(
  path: String,
  options: ResolutionOptions = ResolutionOptions.Default,
): JSONElement? = JsonPath.compile(path).resolveOrNull(this, options)

public fun JSONArray.resolvePathAsStringOrNull(path: String): String? =
  JsonPath.compile(path).resolveOrNull(this)?.asStringOrNull?.toJSONString()

public fun JSONObject.resolveOrNull(
  path: JsonPath,
  options: ResolutionOptions = ResolutionOptions.Default,
): JSONElement? = path.resolveOrNull(this, options)

public fun JSONObject.resolveAsStringOrNull(path: JsonPath): String? =
  path.resolveOrNull(this)?.asStringOrNull?.toJSONString()

public fun JSONObject.resolvePathOrNull(
  path: String,
  options: ResolutionOptions = ResolutionOptions.Default,
): JSONElement? = JsonPath.compile(path).resolveOrNull(this, options)

public fun JSONObject.resolvePathAsStringOrNull(path: String): String? =
  JsonPath.compile(path).resolveOrNull(this)?.asStringOrNull?.toJSONString()

public fun JsonPath.resolveOrNull(
  json: JSONArray,
  options: ResolutionOptions = ResolutionOptions.Default,
): JSONElement? = resolveOrNull<Any>(
  OrgJsonNode(json, isWildcardScope = false),
  options,
)?.let(::JSONElement)

public fun JsonPath.resolveAsStringOrNull(json: JSONArray): String? = resolveOrNull<Any>(
  OrgJsonNode(json, isWildcardScope = false),
)?.let(::JSONElement)?.asStringOrNull?.toJSONString()

public fun JsonPath.resolveOrNull(
  json: JSONObject,
  options: ResolutionOptions = ResolutionOptions.Default,
): JSONElement? = resolveOrNull<Any>(
  OrgJsonNode(json, isWildcardScope = false),
  options,
)?.let(::JSONElement)

public fun JsonPath.resolveAsStringOrNull(json: JSONObject): String? = resolveOrNull<Any>(
  OrgJsonNode(json, isWildcardScope = false),
)?.let(::JSONElement)?.asStringOrNull?.toJSONString()
