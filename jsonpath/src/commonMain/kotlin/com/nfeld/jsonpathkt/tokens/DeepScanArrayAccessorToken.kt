package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.JsonNode
import com.nfeld.jsonpathkt.extension.isNotNullOrMissing
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonArrayBuilder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray

/**
 * Recursive scan for values/objects/arrays found for all [indices] specified. Returns a [JsonArray] containing results found.
 *
 * @param indices indices to retrieve values/objects for
 */
internal data class DeepScanArrayAccessorToken(val indices: List<Int>) : Token {
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
          // first add all requested indices to our results
          indices.forEach { index ->
            ArrayAccessorToken(index).read(node)?.element?.let {
              if (it.isNotNullOrMissing()) {
                result.add(it)
              }
            }
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
