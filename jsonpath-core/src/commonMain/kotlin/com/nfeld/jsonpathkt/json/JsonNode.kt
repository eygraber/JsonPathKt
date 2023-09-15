package com.nfeld.jsonpathkt.json

/**
 * Provides an abstraction for integrating a JSON library into JsonPathKt
 */
public abstract class JsonNode(
  public val element: Any,
  /**
   * Indicates that the current JSONPath context is using the wildcard (*) operator.
   *
   * When `isWildcardScope` is set to `true`, the token will consider all immediate children
   * of the current JSON object or array. This entails that every direct member of an object
   * or every direct element of an array is selected, irrespective of its key or index.
   *
   * ### Example:
   * Given the JSON:
   * ```json
   * {
   *   "a": "valueA",
   *   "b": "valueB",
   *   "c": ["x", "y", "z"]
   * }
   * ```
   * If `isWildcardScope` is true at the root level, both "a" and "b" keys as well as
   * all elements of the "c" array would be considered.
   */
  internal val isWildcardScope: Boolean,
) {
  public abstract val type: JsonType
  public abstract val asArray: List<Any>
  public abstract val asObject: Map<String, Any>
  public abstract val asObjectValues: Collection<Any>
  public abstract val asString: String?

  public abstract val emptyJsonArray: Any

  public abstract val Any?.isNull: Boolean
  internal inline val Any?.isNotNull: Boolean get() = this != null && !isNull

  public abstract val isNotNull: Boolean

  internal fun List<Any>.getIfNotNull(index: Int) = getOrNull(index).takeIf { it.isNotNull }

  internal fun Map<String, Any>.getIfNotNull(key: String): Any? = get(key).takeIf { it.isNotNull }

  public abstract fun createJsonLiteral(content: String): Any

  public abstract fun toJsonArray(list: List<Any>): Any
  internal inline fun buildJsonArray(builder: JsonArrayBuilder.() -> Unit): Any =
    with(JsonArrayBuilder()) {
      builder()
      toJsonArray(elements)
    }

  public abstract fun copy(
    element: Any = this.element,
    isWildcardScope: Boolean = this.isWildcardScope,
  ): JsonNode
}
