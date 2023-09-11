package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.JsonNode
import com.nfeld.jsonpathkt.extension.isNotNullOrMissing
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonArrayBuilder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray

/**
 * Recursive scan for values with keys in [targetKeys] list. Returns a [JsonArray] containing values found.
 *
 * @param targetKeys keys to find values for
 */
internal data class DeepScanObjectAccessorToken(val targetKeys: List<String>) : Token {
  private fun scan(node: JsonNode, result: JsonArrayBuilder) {
    when (val element = node.element) {
      is JsonObject -> {
        // first add all values from keys requested to our result
        targetKeys.forEach { key ->
          ObjectAccessorToken.read(node, key)?.element?.let {
            if (it.isNotNullOrMissing()) {
              result.add(it)
            }
          }
        }

        // recursively scan all underlying objects/arrays
        element.values.forEach {
          if (it.isNotNullOrMissing()) {
            scan(JsonNode(it, isNewRoot = false), result)
          }
        }
      }

      is JsonArray -> {
        element.forEach {
          if (it.isNotNullOrMissing()) {
            scan(JsonNode(it, isNewRoot = false), result)
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
