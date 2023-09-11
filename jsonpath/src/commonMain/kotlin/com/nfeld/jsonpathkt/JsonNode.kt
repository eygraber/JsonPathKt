package com.nfeld.jsonpathkt

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull

data class JsonNode(
  val element: JsonElement,
  val isNewRoot: Boolean,
) {
  init {
    require(!isNewRoot || element is JsonArray || element is JsonNull)
  }
}
