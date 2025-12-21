package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.json.JsonArrayBuilder
import com.nfeld.jsonpathkt.json.JsonNode
import com.nfeld.jsonpathkt.json.JsonType

/**
 * Recursive scan for values/objects/arrays found for all [indices] specified. Returns a JsonArray containing results found.
 *
 * @param indices indices to retrieve values/objects for
 */
internal data class DeepScanArrayAccessorToken(val indices: List<Int>) : Token {
  private fun scan(node: JsonNode, result: JsonArrayBuilder) {
    when (node.type) {
      JsonType.Object ->
        // traverse all key/value pairs and recursively scan underlying objects/arrays
        node.asObjectValues.forEach { value ->
          if (with(node) { value.isNotNull }) {
            scan(node.copy(value, isWildcardScope = false), result)
          }
        }

      JsonType.Array -> when {
        node.isWildcardScope ->
          // no need to add anything on root level, scan down next level
          node.asArray.forEach { element ->
            if (with(node) { element.isNotNull }) {
              scan(node.copy(element, isWildcardScope = false), result)
            }
          }

        else -> {
          // first add all requested indices to our results
          indices.forEach { index ->
            ArrayAccessorToken(index).read(node)?.element?.let { element ->
              if (with(node) { element.isNotNull }) {
                result.add(element)
              }
            }
          }

          // now recursively scan underlying objects/arrays
          node.asArray.forEach { element ->
            if (with(node) { element.isNotNull }) {
              scan(node.copy(element, isWildcardScope = false), result)
            }
          }
        }
      }

      JsonType.Null,
      JsonType.Primitive,
      -> {}
    }
  }

  override fun read(node: JsonNode): JsonNode =
    node.copy(
      element = node.buildJsonArray {
        scan(node, this)
      },
      isWildcardScope = true,
    )
}
