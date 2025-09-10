package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.json.JsonNode
import com.nfeld.jsonpathkt.json.JsonType

/**
 * Accesses value at [key] from JsonObject
 *
 * @param key key to access
 */
@PublishedApi
internal data class ObjectAccessorToken(val key: String) : Token {
  override fun read(node: JsonNode): JsonNode? = read(node, key)

  companion object {
    fun read(node: JsonNode, key: String): JsonNode? = when {
      node.type == JsonType.Object -> with(node) {
        asObject.getIfNotNull(key)?.let {
          node.copy(it, isWildcardScope = false)
        }
      }

      node.type == JsonType.Array && node.isWildcardScope -> {
        // we're at root level and can get children from objects
        node.copy(
          element = node.buildJsonArray {
            node.asArray.forEach { element ->
              with(node.copy(element)) {
                if (type == JsonType.Object) {
                  asObject.getIfNotNull(key)?.let(::add)
                }
              }
            }
          },
          isWildcardScope = true,
        )
      }
      // JsonArray should return null, unless it's the RootLevelArrayNode. This is intentional
      // everything else is scalar and not accessible
      else -> null
    }
  }
}
