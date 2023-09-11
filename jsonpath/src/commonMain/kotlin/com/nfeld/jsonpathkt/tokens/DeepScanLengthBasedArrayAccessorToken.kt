package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.JsonNode
import com.nfeld.jsonpathkt.extension.isNotNullOrMissing
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonArrayBuilder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray

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
    when (val element = node.element) {
      is JsonObject -> {
        // traverse all key/value pairs and recursively scan underlying objects/arrays
        element.values.forEach {
          if (it.isNotNullOrMissing()) {
            scan(JsonNode(it, isNewRoot = false), result)
          }
        }
      }

      is JsonArray -> when {
        node.isNewRoot -> {
          // no need to add anything on root level, scan down next level
          element.forEach {
            if (it.isNotNullOrMissing()) {
              scan(JsonNode(it, isNewRoot = false), result)
            }
          }
        }

        else -> {
          ArrayLengthBasedRangeAccessorToken(startIndex, endIndex, offsetFromEnd)
            .read(node).element.let { resultNode ->
              val resultArray = resultNode as? JsonArray
              resultArray?.forEach { result.add(it) }
            }

          // now recursively scan underlying objects/arrays
          element.forEach {
            if (it.isNotNullOrMissing()) {
              scan(JsonNode(it, isNewRoot = false), result)
            }
          }
        }
      }

      else -> {}
    }
  }

  override fun read(json: JsonNode): JsonNode =
    JsonNode(
      element = buildJsonArray {
        scan(json, this)
      },
      isNewRoot = true,
    )
}
