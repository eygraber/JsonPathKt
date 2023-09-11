package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.JsonNode
import com.nfeld.jsonpathkt.extension.getValueIfNotNullOrMissing
import com.nfeld.jsonpathkt.extension.isNotNullOrMissing
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray

/**
 * Accesses value at [index] from JsonArray
 *
 * @param index index to access, can be negative which means to access from end
 */
internal data class ArrayAccessorToken(val index: Int) : Token {
  override fun read(json: JsonNode): JsonNode? = read(json, index)

  companion object {
    fun read(json: JsonNode, index: Int): JsonNode? = when (val element = json.element) {
      is JsonArray -> when {
        json.isNewRoot -> JsonNode(
          element = buildJsonArray {
            element.forEach { node ->
              read(JsonNode(node, isNewRoot = false), index)?.element?.let { element ->
                if (element.isNotNullOrMissing()) {
                  add(element)
                }
              }
            }
          },
          isNewRoot = true,
        )

        else -> readValueAtIndex(element, index)?.let { JsonNode(it, isNewRoot = false) }
      }

      is JsonNull -> null

      is JsonPrimitive -> {
        when {
          element.isString -> {
            val str = element.content
            if (index < 0) {
              val indexFromLast = str.length + index
              if (indexFromLast >= 0 && indexFromLast < str.length) {
                JsonNode(
                  element = JsonPrimitive(str[indexFromLast].toString()),
                  isNewRoot = false,
                )
              } else {
                null
              }
            } else if (index < str.length) {
              JsonNode(element = JsonPrimitive(str[index].toString()), isNewRoot = false)
            } else {
              null
            }
          }

          else -> null
        }
      }

      else -> null
    }

    private fun readValueAtIndex(arrayNode: JsonArray, index: Int): JsonElement? {
      if (index < 0) {
        val indexFromLast = arrayNode.size + index
        if (indexFromLast >= 0) {
          return arrayNode.getValueIfNotNullOrMissing(indexFromLast)
        }
      }
      return arrayNode.getValueIfNotNullOrMissing(index)
    }
  }
}
