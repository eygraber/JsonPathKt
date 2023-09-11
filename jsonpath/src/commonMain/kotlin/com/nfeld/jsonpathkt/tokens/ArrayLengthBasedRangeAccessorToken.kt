package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.JsonNode
import com.nfeld.jsonpathkt.extension.isNotNullOrMissing
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.buildJsonArray

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
  override fun read(json: JsonNode): JsonNode {
    val token = when (val element = json.element) {
      is JsonArray -> when {
        json.isNewRoot -> {
          return JsonNode(
            element = buildJsonArray {
              element.forEach { node ->
                val nextNode = read(JsonNode(node, isNewRoot = false))
                when (val nextNodeElement = nextNode.element) {
                  is JsonArray -> nextNodeElement.forEach(::add)
                  else -> if (nextNodeElement.isNotNullOrMissing()) add(nextNodeElement)
                }
              }
            },
            isNewRoot = true,
          )
        }

        else -> toMultiArrayAccessorToken(element)
      }

      else -> null
    }
    return token?.read(json.copy(isNewRoot = false)) ?: JsonNode(element = JsonArray(emptyList()), isNewRoot = true)
  }

  /**
   * We know the size of the array during runtime so we can recreate the MultiArrayAccessorToken to read the values
   */
  fun toMultiArrayAccessorToken(json: JsonArray): MultiArrayAccessorToken? {
    val size = json.size
    val start = if (startIndex < 0) {
      val start = size + startIndex
      if (start < 0) 0 else start // even if we're out of bounds at start, always start from first item
    } else {
      startIndex
    }

    // use endIndex if we have it, otherwise calculate from json array length
    val endInclusive = if (endIndex != null) {
      endIndex - 1
    } else {
      size + offsetFromEnd - 1
    }

    return if (start in 0..endInclusive) {
      MultiArrayAccessorToken(IntRange(start, endInclusive).toList())
    } else {
      null
    }
  }
}
