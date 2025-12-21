package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.json.JsonNode
import com.nfeld.jsonpathkt.json.JsonType

/**
 * Accesses value at [index] from JsonArray
 *
 * @param index index to access, can be negative which means to access from end
 */
@PublishedApi
internal data class ArrayAccessorToken(val index: Int) : Token {
  override fun read(node: JsonNode): JsonNode? = read(node, index)

  companion object {
    fun read(node: JsonNode, index: Int): JsonNode? = when (node.type) {
      JsonType.Array -> when {
        node.isWildcardScope -> node.copy(
          element = node.buildJsonArray {
            node.asArray.forEach { element ->
              read(
                node = node.copy(
                  element = element,
                  isWildcardScope = false,
                ),
                index = index,
              )?.let { nextNode ->
                if (nextNode.isNotNull) {
                  add(nextNode.element)
                }
              }
            }
          },
          isWildcardScope = true,
        )

        else -> node.readValueAtIndex(index)?.let { node.copy(it, isWildcardScope = false) }
      }

      JsonType.Null -> null

      JsonType.Primitive -> when (val str = node.asString) {
        null -> null

        else -> if (index < 0) {
          val indexFromLast = str.length + index
          if (indexFromLast >= 0 && indexFromLast < str.length) {
            node.copy(
              element = node.createJsonLiteral(str[indexFromLast].toString()),
              isWildcardScope = false,
            )
          } else {
            null
          }
        } else if (index < str.length) {
          node.copy(
            element = node.createJsonLiteral(str[index].toString()),
            isWildcardScope = false,
          )
        } else {
          null
        }
      }

      JsonType.Object -> null
    }

    private fun JsonNode.readValueAtIndex(index: Int): Any? {
      val array = asArray

      if (index < 0) {
        val indexFromLast = array.size + index
        if (indexFromLast >= 0) {
          return array.getIfNotNull(indexFromLast)
        }
      }
      return array.getIfNotNull(index)
    }
  }
}
