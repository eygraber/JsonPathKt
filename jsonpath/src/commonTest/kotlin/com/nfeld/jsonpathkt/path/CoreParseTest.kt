package com.nfeld.jsonpathkt.path

import com.nfeld.jsonpathkt.BOOKS_JSON
import com.nfeld.jsonpathkt.JsonPath
import com.nfeld.jsonpathkt.LARGE_JSON
import com.nfeld.jsonpathkt.SMALL_JSON
import com.nfeld.jsonpathkt.SMALL_JSON_ARRAY
import com.nfeld.jsonpathkt.extension.read
import com.nfeld.jsonpathkt.readTree
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test

class CoreParseTest {
  @Test
  fun parse_should_be_null_on_parse_failure() {
    JsonPath.parse("5{}") shouldBe null
    JsonPath.parse("5[]") shouldBe null
    JsonPath.parse("{") shouldBe null
    JsonPath.parse("") shouldBe null
    JsonPath.parse("{]") shouldBe null
    JsonPath.parse("[}") shouldBe null
    JsonPath.parse("null") shouldBe null
    JsonPath.parse(null) shouldBe null
  }

  @Test
  fun parse_should_be_null_if_root_node_is_Null() {
    JsonPath("$").read(JsonNull) shouldBe null
  }

  // What's the reason for this?
//      @Test
//      fun parse_should_be_null_when_parsing_root_string_but_without_quotes() {
//          JsonPath.parse("hello") shouldBe null
//      }

  @Test
  fun parse_should_parse_root_string_with_quotes() {
    JsonPath.parse(""""hello"""") shouldBe JsonPrimitive("hello")
    JsonPath.parse(""""hello"""")!!.read<String>("$") shouldBe "hello"
  }

  @Test
  fun parse_should_parse_root_values_other_than_String() {
    JsonPath.parse("4") shouldBe JsonPrimitive(4)
    JsonPath.parse("4")!!.read<Int>("$") shouldBe 4
    JsonPath.parse("4.76") shouldBe JsonPrimitive(4.76)
    JsonPath.parse("4.76")!!.read<Double>("$") shouldBe 4.76
    JsonPath.parse("true") shouldBe JsonPrimitive(true)
    JsonPath.parse("true")!!.read<Boolean>("$") shouldBe true
    JsonPath.parse("false") shouldBe JsonPrimitive(false)
    JsonPath.parse("false")!!.read<Boolean>("$") shouldBe false
  }

  @Test
  fun parse_should_be_able_to_get_JsonObject() {
    JsonPath.parse(SMALL_JSON)!!.read<JsonObject>("$") shouldBe readTree(SMALL_JSON)
  }

  @Test
  fun parse_should_be_able_to_get_JsonArray() {
    JsonPath.parse(SMALL_JSON_ARRAY)!!.read<JsonArray>("$") shouldBe readTree(
      SMALL_JSON_ARRAY,
    )
  }

  @Test
  fun parse_should_be_able_to_get_inner_JsonObjects() {
    val json = """[{"outer": {"inner": 9} }]"""
    JsonPath.parse(json)!!.read<JsonObject>("$[0]") shouldBe readTree(json).jsonArray[0]
    JsonPath.parse(json)!!
      .read<JsonObject>("$[0].outer") shouldBe readTree(json).jsonArray[0].jsonObject["outer"]
  }

  @Test
  fun parse_should_get_values_deep_in_JSON() {
    JsonPath.parse(LARGE_JSON)!!
      .read<String>("$[0].friends[1].other.a.b['c']") shouldBe "yo"
    JsonPath.parse(LARGE_JSON)!!
      .read<String>("$[0].friends[-1]['name']") shouldBe "Harrell Pratt"
  }

  @Test
  fun parse_should_preserve_order() {
    JsonPath.parse(BOOKS_JSON)!!.read<List<Double>>("$.store..price") shouldBe listOf(
      8.95,
      12.99,
      8.99,
      22.99,
      19.95,
    )
    JsonPath.parse("""{"d": 4, "f": 6, "e": 5, "a": 1, "b": 2, "c": 3}""")!!
      .read<List<Int>>("$.*") shouldBe listOf(4, 6, 5, 1, 2, 3)
  }
}
