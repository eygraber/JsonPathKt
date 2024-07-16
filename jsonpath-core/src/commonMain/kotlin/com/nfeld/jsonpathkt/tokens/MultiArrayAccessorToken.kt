package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.json.JsonNode
import com.nfeld.jsonpathkt.json.JsonType

/**
 * Accesses values at [indices] from JsonArray. When read, value returned will be JsonArray of values
 * at requested indices in given order.
 *
 * If an index is present in [runtimeRangeIndices] then it means that the range can only be calculated at runtime.
 * It indicates that the index be considered the start index of the range and the following index will be the end.
 *
 * @param indices indices to access, can be negative which means to access from end
 * @param runtimeRangeIndices indices that indicate a range that needs to be generated at runtime
 */
internal data class MultiArrayAccessorToken(
  val indices: List<Int>,
  val runtimeRangeIndices: Set<Int> = emptySet(),
) : Token {
  override fun read(node: JsonNode): JsonNode {
    val result = when {
      node.isWildcardScope -> node.buildJsonArray {
        node.asArray.forEach { element ->
          runtimeIndices(node).forEach { index ->
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
        runtimeIndices(node).forEach { index ->
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

  private fun runtimeIndices(node: JsonNode): List<Int> {
    if (runtimeRangeIndices.isEmpty()) return indices
    if (node.type != JsonType.Array) return indices

    val array = node.asArray

    val runtimeIndices = mutableListOf<Int>()
    var cursor = 0
    while (cursor < indices.size) {
      if (cursor in runtimeRangeIndices) {
        val startIndex = indices[cursor]
        val end = indices[cursor + 1]
        val isEndNegativeOrZero = end <= 0
        val offsetFromEnd = if (isEndNegativeOrZero) end else 0
        val endIndex = if (!isEndNegativeOrZero) end else null

        cursor += 2

        val start = if (startIndex < 0) {
          val start = array.size + startIndex
          if (start < 0) 0 else start // even if we're out of bounds at start, always start from first item
        } else {
          startIndex
        }

        // use endIndex if we have it, otherwise calculate from json array length
        val endInclusive = if (endIndex != null) {
          endIndex - 1
        } else {
          array.size + offsetFromEnd - 1
        }

        if (start in 0..endInclusive) {
          runtimeIndices += IntRange(start, endInclusive)
        }
      } else {
        runtimeIndices += indices[cursor++]
      }
    }

    return runtimeIndices
  }
}
