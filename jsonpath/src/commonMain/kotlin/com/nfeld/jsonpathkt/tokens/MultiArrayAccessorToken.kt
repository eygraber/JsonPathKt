package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.JsonNode
import com.nfeld.jsonpathkt.extension.isNotNullOrMissing
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.buildJsonArray

/**
 * Accesses values at [indices] from JsonArray. When read, value returned will be JsonArray of values
 * at requested indices in given order.
 *
 * @param indices indices to access, can be negative which means to access from end
 */
internal data class MultiArrayAccessorToken(val indices: List<Int>) : Token {
  override fun read(json: JsonNode): JsonNode {
    val result = when {
      json.isNewRoot -> buildJsonArray {
        (json.element as JsonArray).forEach { node ->
          indices.forEach { index ->
            ArrayAccessorToken.read(
              JsonNode(node, isNewRoot = false),
              index,
            )?.element?.let { element ->
              if (element.isNotNullOrMissing()) {
                add(element)
              }
            }
          }
        }
      }

      else -> buildJsonArray {
        indices.forEach { index ->
          ArrayAccessorToken.read(json, index)?.element?.let { element ->
            if (element.isNotNullOrMissing()) {
              add(element)
            }
          }
        }
      }
    }
    return JsonNode(result, isNewRoot = true)
  }
}
