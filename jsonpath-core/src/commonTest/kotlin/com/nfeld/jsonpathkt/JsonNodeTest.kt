package com.nfeld.jsonpathkt

import kotlin.test.Test
import kotlin.test.assertEquals

class JsonNodeTest {
  @Test
  fun should_read_root_JsonArray() {
    val jsonObj = SMALL_JSON_ARRAY.asJson
    assertEquals(2, jsonObj.resolveAsType("$[1]"))
  }

  @Test
  fun should_read_root_JsonObject() {
    val jsonObj = SMALL_JSON.asJson
    assertEquals(5, jsonObj.resolveAsType("$['key']"))
  }
}
