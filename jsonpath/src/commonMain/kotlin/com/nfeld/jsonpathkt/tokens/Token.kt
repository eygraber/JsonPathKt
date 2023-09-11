package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.JsonNode

interface Token {
  /**
   * Takes in JsonElement and outputs next JsonElement or value by evaluating token against current object/array in path
   */
  fun read(json: JsonNode): JsonNode?
}
