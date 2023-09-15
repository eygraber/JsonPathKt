package com.nfeld.jsonpathkt.path

import com.nfeld.jsonpathkt.resolveAsType
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlin.test.Test

class MapObjectsTest {
  @Test
  fun should_be_Map() {
    Json.parseToJsonElement("""{"a": {"b": "yo"}}""")
      .resolveAsType<Map<String, Map<String, String>>>("$") shouldBe mapOf("a" to mapOf("b" to "yo"))
    Json.parseToJsonElement("""{"a": {"b": "yo"}}""")
      .resolveAsType<Map<String, String>>("$.a") shouldBe mapOf("b" to "yo")
  }
}
