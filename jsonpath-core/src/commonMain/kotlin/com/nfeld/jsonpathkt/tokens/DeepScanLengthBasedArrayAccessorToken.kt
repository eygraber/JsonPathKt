package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.json.JsonArrayBuilder
import com.nfeld.jsonpathkt.json.JsonNode
import com.nfeld.jsonpathkt.json.JsonType

/**
 * Recursive scan for values/objects/arrays from [JsonArray] in range from [startIndex] to either [endIndex] or [offsetFromEnd] from end.
 * When read, value returned will be JsonArray of values at requested indices in order of values in range. Returns a JsonArray containing results found.
 *
 * @param startIndex starting index of range, inclusive. Can be negative.
 * @param endIndex ending index of range, exclusive. Null if using [offsetFromEnd]
 * @param offsetFromEnd offset of values from end of array. 0 if using [endIndex]
 */
internal data class DeepScanLengthBasedArrayAccessorToken(
  val startIndex: Int,
  val endIndex: Int? = null,
  val offsetFromEnd: Int = 0,
) : Token {
  private fun scan(node: JsonNode, result: JsonArrayBuilder) {
    when (node.type) {
      JsonType.Object -> {
        // traverse all key/value pairs and recursively scan underlying objects/arrays
        node.asObjectValues.forEach { value ->
          if (with(node) { value.isNotNull }) {
            scan(node.copy(value, isWildcardScope = false), result)
          }
        }
      }

      JsonType.Array -> when {
        node.isWildcardScope -> {
          // no need to add anything on root level, scan down next level
          node.asArray.forEach { element ->
            if (with(node) { element.isNotNull }) {
              scan(node.copy(element, isWildcardScope = false), result)
            }
          }
        }

        else -> {
          ArrayLengthBasedRangeAccessorToken(startIndex, endIndex, offsetFromEnd)
            .read(node).let { resultNode ->
              resultNode.asArray.forEach { element ->
                result.add(element)
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
