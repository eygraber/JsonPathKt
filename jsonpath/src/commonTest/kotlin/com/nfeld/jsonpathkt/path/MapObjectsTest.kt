package com.nfeld.jsonpathkt.path

import com.nfeld.jsonpathkt.JsonPath
import com.nfeld.jsonpathkt.extension.read
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class MapObjectsTest {
  @Test
  fun should_be_Map() {
    JsonPath.parse("""{"a": {"b": "yo"}}""")!!
      .read<Map<String, Map<String, String>>>("$") shouldBe mapOf("a" to mapOf("b" to "yo"))
    JsonPath.parse("""{"a": {"b": "yo"}}""")!!
      .read<Map<String, String>>("$.a") shouldBe mapOf("b" to "yo")
  }
}
