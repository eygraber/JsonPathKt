package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.json.JsonNode
import com.nfeld.jsonpathkt.json.JsonType

/**
 * Accesses values at [keys] from JsonObject. When read, value returned will be JsonObject
 * containing key/value pairs requested. Keys that are null or don't exist won't be added in Object
 *
 * @param keys keys to access for which key/values to return
 */
internal data class MultiObjectAccessorToken(val keys: List<String>) : Token {
  override fun read(node: JsonNode): JsonNode = when {
    node.type == JsonType.Object ->
      // Going from an object to a list always creates a root level list
      node.copy(
        element = node.buildJsonArray {
          keys.forEach { key ->
            with(node) {
              asObject.getIfNotNull(key)?.let(::add)
            }
          }
        },
        isWildcardScope = true,
      )

    node.type == JsonType.Array && node.isWildcardScope -> node.copy(
      element = node.buildJsonArray {
        node.asArray.forEach { element ->
          keys.forEach { key ->
            ObjectAccessorToken.read(
              node = node.copy(
                element = element,
                isWildcardScope = false,
              ),
              key,
            )?.let { nextNode ->
              if (nextNode.isNotNull) {
                add(nextNode.element)
              }
            }
          }
        }
      },
      isWildcardScope = true,
    )

    else -> node.copy(node.emptyJsonArray, isWildcardScope = true)
  }
}
