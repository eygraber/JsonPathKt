package com.nfeld.jsonpathkt

import com.nfeld.jsonpathkt.extension.read
import kotlin.test.Test
import kotlin.test.assertEquals

class JsonNodeTest {
  @Test
  fun should_read_root_JsonArray() {
    val jsonObj = readTree(SMALL_JSON_ARRAY)
    assertEquals(2, jsonObj.read(JsonPath("$[1]"))!!)
    assertEquals(2, jsonObj.read("$[1]")!!)
  }

  @Test
  fun should_read_root_JsonObject() {
    val jsonObj = readTree(SMALL_JSON)
    assertEquals(5, jsonObj.read(JsonPath("$['key']"))!!)
    assertEquals(5, jsonObj.read("$['key']")!!)
  }
}
