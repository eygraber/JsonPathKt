package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.json.JsonArrayBuilder
import com.nfeld.jsonpathkt.json.JsonNode
import com.nfeld.jsonpathkt.json.JsonType

/**
 * Recursive scan for values with keys in [targetKeys] list. Returns a JsonArray containing values found.
 *
 * @param targetKeys keys to find values for
 */
internal data class DeepScanObjectAccessorToken(val targetKeys: List<String>) : Token {
  private fun scan(node: JsonNode, result: JsonArrayBuilder) {
    when (node.type) {
      JsonType.Object -> {
        // first add all values from keys requested to our result
        targetKeys.forEach { key ->
          ObjectAccessorToken.read(node, key)?.let {
            if (it.isNotNull) {
              result.add(it.element)
            }
          }
        }

        // recursively scan all underlying objects/arrays
        node.asObjectValues.forEach { value ->
          if (with(node) { value.isNotNull }) {
            scan(node.copy(value, isWildcardScope = false), result)
          }
        }
      }

      JsonType.Array -> {
        node.asArray.forEach { element ->
          if (with(node) { element.isNotNull }) {
            scan(node.copy(element, isWildcardScope = false), result)
          }
        }
      }

      else -> {}
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
