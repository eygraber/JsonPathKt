package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.JsonNode
import com.nfeld.jsonpathkt.extension.getValueIfNotNullOrMissing
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray

/**
 * Accesses value at [key] from [JsonObject]
 *
 * @param key key to access
 */
internal data class ObjectAccessorToken(val key: String) : Token {
  override fun read(json: JsonNode): JsonNode? = read(json, key)

  companion object {
    fun read(json: JsonNode, key: String): JsonNode? = when {
      json.element is JsonObject -> json.element.getValueIfNotNullOrMissing(key)?.let {
        JsonNode(it, isNewRoot = false)
      }

      json.element is JsonArray && json.isNewRoot -> {
        // we're at root level and can get children from objects
        JsonNode(
          element = buildJsonArray {
            json.element.forEach { node ->
              if (node is JsonObject) {
                node.getValueIfNotNullOrMissing(key)?.let {
                  add(it)
                }
              }
            }
          },
          isNewRoot = true,
        )
      }
      // JsonArray should return null, unless it's the RootLevelArrayNode. This is intentional
      // everything else is scalar and not accessible
      else -> null
    }
  }
}
