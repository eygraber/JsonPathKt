package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.JsonNode
import com.nfeld.jsonpathkt.extension.getValueIfNotNullOrMissing
import com.nfeld.jsonpathkt.extension.isNotNullOrMissing
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray

/**
 * Accesses values at [keys] from [JsonObject]. When read, value returned will be [JsonObject]
 * containing key/value pairs requested. Keys that are null or don't exist won't be added in Object
 *
 * @param keys keys to access for which key/values to return
 */
internal data class MultiObjectAccessorToken(val keys: List<String>) : Token {
  override fun read(json: JsonNode): JsonNode = when {
    json.element is JsonObject -> {
      // Going from an object to a list always creates a root level list
      JsonNode(
        element = buildJsonArray {
          keys.forEach {
            json.element.getValueIfNotNullOrMissing(it)?.let(::add)
          }
        },
        isNewRoot = true,
      )
    }

    json.element is JsonArray && json.isNewRoot -> {
      JsonNode(
        element = buildJsonArray {
          json.element.forEach { node ->
            keys.forEach { key ->
              ObjectAccessorToken.read(JsonNode(node, isNewRoot = false), key)?.element?.let {
                if (it.isNotNullOrMissing()) {
                  add(it)
                }
              }
            }
          }
        },
        isNewRoot = true,
      )
    }

    else -> JsonNode(JsonArray(emptyList()), isNewRoot = true)
  }
}
