package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.JsonNode
import com.nfeld.jsonpathkt.extension.isNotNullOrMissing
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonArrayBuilder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray

internal class DeepScanWildcardToken : Token {
  private fun scan(node: JsonNode, result: JsonArrayBuilder) {
    when {
      node.isNewRoot -> {
        // no need to add anything on root level, scan down next level
        (node.element as JsonArray).forEach {
          if (it.isNotNullOrMissing()) {
            scan(JsonNode(it, isNewRoot = false), result)
          }
        }
      }

      node.element is JsonObject || node.element is JsonArray -> {
        WildcardToken().read(node).let { nextNode ->
          if (nextNode.element is JsonArray) {
            nextNode.element.forEach {
              if (it.isNotNullOrMissing()) {
                result.add(it)
              }
            }
          }
        }

        // now recursively scan underlying objects/arrays
        when (node.element) {
          is JsonArray -> node.element.forEach {
            if (it.isNotNullOrMissing()) {
              scan(JsonNode(it, isNewRoot = false), result)
            }
          }

          is JsonObject -> node.element.values.forEach {
            if (it.isNotNullOrMissing()) {
              scan(JsonNode(it, isNewRoot = false), result)
            }
          }

          else -> {}
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

  override fun toString(): String = "DeepScanWildcardToken"
  override fun hashCode(): Int = toString().hashCode()
  override fun equals(other: Any?): Boolean = other is DeepScanWildcardToken
}
