package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.json.JsonNode
import com.nfeld.jsonpathkt.json.JsonType

/**
 * Returns all values from an Object, or the same list
 */
@PublishedApi
internal data object WildcardToken : Token {
  override fun read(node: JsonNode): JsonNode = when (node.type) {
    JsonType.Object -> {
      node.copy(
        element = node.buildJsonArray {
          node.asObjectValues.forEach { value ->
            if (with(node) { value.isNotNull }) {
              add(value)
            }
          }
        },
        isWildcardScope = true,
      )
    }

    JsonType.Array -> {
      if (!node.isWildcardScope) {
        // copy over children into our special JsonArray to hold underlying items
        node.copy(isWildcardScope = true)
      } else {
        node.copy(
          element = node.buildJsonArray {
            // iterate through each item and move everything up one level
            node.asArray.forEach { element ->
              val elementNode = node.copy(element)
              when (elementNode.type) {
                JsonType.Object -> {
                  elementNode.asObjectValues.forEach { value ->
                    if (with(elementNode) { value.isNotNull }) {
                      add(value)
                    }
                  }
                }

                JsonType.Array -> {
                  elementNode.asArray.forEach { subElement ->
                    if (with(elementNode) { subElement.isNotNull }) {
                      add(subElement)
                    }
                  }
                }

                // anything else gets dropped since it's on rootmost level
                else -> {}
              }
            }
          },
          isWildcardScope = true,
        )
      }
    }

    else -> node.copy(isWildcardScope = false)
  }
}
