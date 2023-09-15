package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.json.JsonArrayBuilder
import com.nfeld.jsonpathkt.json.JsonNode
import com.nfeld.jsonpathkt.json.JsonType

internal class DeepScanWildcardToken : Token {
  private fun scan(node: JsonNode, result: JsonArrayBuilder) {
    when {
      node.isWildcardScope -> {
        // no need to add anything on root level, scan down next level
        node.asArray.forEach { element ->
          if (with(node) { element.isNotNull }) {
            scan(node.copy(element, isWildcardScope = false), result)
          }
        }
      }

      node.type.isArrayOrObject -> {
        WildcardToken().read(node).let { nextNode ->
          nextNode.asArray.forEach { element ->
            if (with(node) { element.isNotNull }) {
              result.add(element)
            }
          }
        }

        // now recursively scan underlying objects/arrays
        when (node.type) {
          JsonType.Array -> node.asArray.forEach { element ->
            if (with(node) { element.isNotNull }) {
              scan(node.copy(element, isWildcardScope = false), result)
            }
          }

          JsonType.Object -> node.asObjectValues.forEach { value ->
            if (with(node) { value.isNotNull }) {
              scan(node.copy(value, isWildcardScope = false), result)
            }
          }

          else -> {}
        }
      }

      else -> {}
    }
  }

  override fun read(node: JsonNode): JsonNode =
    node.copy(
      element = node.buildJsonArray {
        scan(node, this)
      },
      isWildcardScope = true,
    )

  override fun toString(): String = "DeepScanWildcardToken"
  override fun hashCode(): Int = toString().hashCode()
  override fun equals(other: Any?): Boolean = other is DeepScanWildcardToken
}
