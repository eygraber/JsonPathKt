package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.JsonNode
import com.nfeld.jsonpathkt.extension.isNotNullOrMissing
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray

/**
 * Returns all values from an Object, or the same list
 */
internal class WildcardToken : Token {
  override fun read(json: JsonNode): JsonNode = when (val element = json.element) {
    is JsonObject -> {
      JsonNode(
        element = buildJsonArray {
          element.values.forEach {
            if (it.isNotNullOrMissing()) {
              add(it)
            }
          }
        },
        isNewRoot = true,
      )
    }

    is JsonArray -> {
      if (!json.isNewRoot) {
        // copy over children into our special JsonArray to hold underlying items
        json.copy(isNewRoot = true)
      } else {
        JsonNode(
          element = buildJsonArray {
            // iterate through each item and move everything up one level
            element.forEach { element ->
              when (element) {
                is JsonObject -> {
                  element.values.forEach {
                    if (it.isNotNullOrMissing()) {
                      add(it)
                    }
                  }
                }

                is JsonArray -> {
                  // move all items from this node to result node
                  element.forEach {
                    if (it.isNotNullOrMissing()) {
                      add(it)
                    }
                  }
                }
                // anything else gets dropped since it's on rootmost level
                else -> {}
              }
            }
          },
          isNewRoot = true,
        )
      }
    }

    else -> json.copy(isNewRoot = false)
  }

  override fun toString(): String = "WildcardToken"
  override fun hashCode(): Int = toString().hashCode()
  override fun equals(other: Any?): Boolean = other is WildcardToken
}
