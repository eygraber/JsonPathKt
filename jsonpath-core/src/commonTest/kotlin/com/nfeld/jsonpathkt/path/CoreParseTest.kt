package com.nfeld.jsonpathkt.path

import com.nfeld.jsonpathkt.BOOKS_JSON
import com.nfeld.jsonpathkt.LARGE_PARSED_JSON
import com.nfeld.jsonpathkt.SMALL_JSON
import com.nfeld.jsonpathkt.SMALL_JSON_ARRAY
import com.nfeld.jsonpathkt.asJson
import com.nfeld.jsonpathkt.kotlinx.resolvePathOrNull
import com.nfeld.jsonpathkt.resolveAsType
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test

class CoreParseTest {
  @Test
  fun parse_should_be_null_if_root_node_is_Null() {
    JsonNull.resolvePathOrNull("$") shouldBe null
  }

  @Test
  fun parse_should_parse_root_string_with_quotes() {
    Json.parseToJsonElement(""""hello"""").resolveAsType<String>("$") shouldBe "hello"
  }

  @Test
  fun parse_should_parse_root_values_other_than_String() {
    Json.parseToJsonElement("4").resolveAsType<Int>("$") shouldBe 4
    Json.parseToJsonElement("4.76").resolveAsType<Double>("$") shouldBe 4.76
    Json.parseToJsonElement("true").resolveAsType<Boolean>("$") shouldBe true
    Json.parseToJsonElement("false").resolveAsType<Boolean>("$") shouldBe false
  }

  @Test
  fun parse_should_be_able_to_get_JsonObject() {
    Json.parseToJsonElement(SMALL_JSON).resolveAsType<JsonObject>("$") shouldBe SMALL_JSON.asJson
  }

  @Test
  fun parse_should_be_able_to_get_JsonArray() {
    Json.parseToJsonElement(SMALL_JSON_ARRAY).resolveAsType<JsonArray>("$") shouldBe SMALL_JSON_ARRAY.asJson
  }

  @Test
  fun parse_should_be_able_to_get_inner_JsonObjects() {
    val json = """[{"outer": {"inner": 9} }]"""
    Json.parseToJsonElement(json).resolveAsType<JsonObject>("$[0]") shouldBe json.asJson.jsonArray[0]
    Json.parseToJsonElement(json)
      .resolveAsType<JsonObject>("$[0].outer") shouldBe json.asJson.jsonArray[0].jsonObject["outer"]
  }

  @Test
  fun parse_should_get_values_deep_in_JSON() {
    LARGE_PARSED_JSON
      .resolveAsType<String>("$[0].friends[1].other.a.b['c']") shouldBe "yo"
    LARGE_PARSED_JSON
      .resolveAsType<String>("$[0].friends[-1]['name']") shouldBe "Harrell Pratt"
  }

  @Test
  fun parse_should_preserve_order() {
    Json.parseToJsonElement(BOOKS_JSON).resolveAsType<List<Double>>("$.store..price") shouldBe listOf(
      8.95,
      12.99,
      8.99,
      22.99,
      19.95,
    )
    Json.parseToJsonElement("""{"d": 4, "f": 6, "e": 5, "a": 1, "b": 2, "c": 3}""")
      .resolveAsType<List<Int>>("$.*") shouldBe listOf(4, 6, 5, 1, 2, 3)
  }
}
