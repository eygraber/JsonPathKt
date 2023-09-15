package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.json.JsonNode
import com.nfeld.jsonpathkt.json.JsonType

/**
 * Accesses values from JsonArray in range from [startIndex] to either [endIndex] or [offsetFromEnd] from end.
 * When read, value returned will be JsonArray of values at requested indices in order of values in range.
 *
 * @param startIndex starting index of range, inclusive. Can be negative.
 * @param endIndex ending index of range, exclusive. Null if using [offsetFromEnd]. Can be positive only
 * @param offsetFromEnd offset of values from end of array. 0 if using [endIndex]. Can be negative only
 */
internal data class ArrayLengthBasedRangeAccessorToken(
  val startIndex: Int,
  val endIndex: Int? = null,
  val offsetFromEnd: Int = 0,
) : Token {
  override fun read(node: JsonNode): JsonNode {
    val token = when (node.type) {
      JsonType.Array -> when {
        node.isWildcardScope -> {
          return node.copy(
            element = node.buildJsonArray {
              node.asArray.forEach { element ->
                val nextNode = read(node.copy(element, isWildcardScope = false))
                when (nextNode.type) {
                  JsonType.Array -> nextNode.asArray.forEach(::add)
                  else -> if (nextNode.isNotNull) add(nextNode.element)
                }
              }
            },
            isWildcardScope = true,
          )
        }

        else -> toMultiArrayAccessorToken(node)
      }

      else -> null
    }
    return token?.read(
      node.copy(isWildcardScope = false),
    ) ?: node.copy(
      element = node.emptyJsonArray,
      isWildcardScope = true,
    )
  }

  /**
   * We know the size of the array during runtime so we can recreate the MultiArrayAccessorToken to read the values
   */
  fun toMultiArrayAccessorToken(node: JsonNode): MultiArrayAccessorToken? {
    val array = node.asArray
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

    return if (start in 0..endInclusive) {
      MultiArrayAccessorToken(IntRange(start, endInclusive).toList())
    } else {
      null
    }
  }
}
