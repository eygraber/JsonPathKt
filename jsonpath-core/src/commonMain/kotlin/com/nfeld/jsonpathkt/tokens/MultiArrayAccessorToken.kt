package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.json.JsonNode

/**
 * Accesses values at [indices] from JsonArray. When read, value returned will be JsonArray of values
 * at requested indices in given order.
 *
 * @param indices indices to access, can be negative which means to access from end
 */
internal data class MultiArrayAccessorToken(val indices: List<Int>) : Token {
  override fun read(node: JsonNode): JsonNode {
    val result = when {
      node.isWildcardScope -> node.buildJsonArray {
        node.asArray.forEach { element ->
          indices.forEach { index ->
            ArrayAccessorToken.read(
              node.copy(element, isWildcardScope = false),
              index,
            )?.let { nextNode ->
              if (nextNode.isNotNull) {
                add(nextNode.element)
              }
            }
          }
        }
      }

      else -> node.buildJsonArray {
        indices.forEach { index ->
          ArrayAccessorToken.read(node, index)?.let { nextNode ->
            if (nextNode.isNotNull) {
              add(nextNode.element)
            }
          }
        }
      }
    }
    return node.copy(result, isWildcardScope = true)
  }
}
