package com.nfeld.jsonpathkt.path

import com.nfeld.jsonpathkt.FAMILY_JSON
import com.nfeld.jsonpathkt.LARGE_PARSED_JSON
import com.nfeld.jsonpathkt.resolveAsType
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test

class ArrayRangesTest {
  @Test
  fun parse_should_handle_array_range_from_start() {
    LARGE_PARSED_JSON.resolveAsType<List<String>>("$[0]['tags'][:3]") shouldBe listOf(
      "occaecat",
      "mollit",
      "ullamco",
    )
    LARGE_PARSED_JSON.resolveAsType<List<String>>("$[0]['tags'][:-4]") shouldBe listOf(
      "occaecat",
      "mollit",
      "ullamco",
    )
  }

  @Test
  fun parse_should_handle_array_range_to_end() {
    LARGE_PARSED_JSON
      .resolveAsType<List<String>>("$[0]['tags'][5:]") shouldBe listOf("laboris", "qui")
    LARGE_PARSED_JSON
      .resolveAsType<List<String>>("$[0]['tags'][-2:]") shouldBe listOf("laboris", "qui")
  }

  @Test
  fun parse_should_handle_specified_range_exclusive_at_end() {
    LARGE_PARSED_JSON
      .resolveAsType<List<String>>("$[0]['tags'][3:5]") shouldBe listOf("labore", "cillum")
    LARGE_PARSED_JSON.resolveAsType<List<String>>("$[0]['tags'][3:-1]") shouldBe listOf(
      "labore",
      "cillum",
      "laboris",
    )
    LARGE_PARSED_JSON.resolveAsType<List<String>>("$[0]['tags'][-6:4]") shouldBe listOf(
      "mollit",
      "ullamco",
      "labore",
    )
    LARGE_PARSED_JSON.resolveAsType<List<String>>("$[0]['tags'][-3:-1]") shouldBe listOf(
      "cillum",
      "laboris",
    )
  }

  @Test
  fun parse_should_return_range_items_up_to_end_if_end_index_out_of_bounds() {
    LARGE_PARSED_JSON
      .resolveAsType<List<String>>("$[0]['tags'][5:30]") shouldBe listOf("laboris", "qui")
  }

  @Test
  fun parse_should_return_range_items_up_from_start_if_start_index_out_of_bounds() {
    Json.parseToJsonElement("""["first", "second", "third"]""")
      .resolveAsType<List<String>>("$[-4:]") shouldBe listOf("first", "second", "third")
  }

  @Test
  fun parse_should_return_empty_list_if_used_on_JSON_object() {
    Json.parseToJsonElement("""{"key":3}""").resolveAsType<JsonElement>("$[1:3]")?.toString() shouldBe "[]"
  }

  @Test
  fun parse_should_get_all_items_in_list() {
    Json.parseToJsonElement("""["first", "second"]""").resolveAsType<List<String>>("$[:]") shouldBe listOf(
      "first",
      "second",
    )
    Json.parseToJsonElement("""["first", "second"]""").resolveAsType<List<String>>("$[0:]") shouldBe listOf(
      "first",
      "second",
    )
    Json.parseToJsonElement("""["first", "second"]""").resolveAsType<List<String>>("$") shouldBe listOf(
      "first",
      "second",
    )

    val expected = listOf(
      mapOf(
        "name" to JsonPrimitive("Thomas"),
        "age" to JsonPrimitive(13),
      ),
      mapOf(
        "name" to JsonPrimitive("Mila"),
        "age" to JsonPrimitive(18),
      ),
      mapOf(
        "name" to JsonPrimitive("Konstantin"),
        "age" to JsonPrimitive(29),
        "nickname" to JsonPrimitive("Kons"),
      ),
      mapOf(
        "name" to JsonPrimitive("Tracy"),
        "age" to JsonPrimitive(4),
      ),
    )
    Json.parseToJsonElement(FAMILY_JSON)
      .resolveAsType<List<Map<String, JsonPrimitive>>>("$.family.children[:]") shouldBe expected
    Json.parseToJsonElement(FAMILY_JSON)
      .resolveAsType<List<Map<String, JsonPrimitive>>>("$.family.children[0:]") shouldBe expected
  }

  @Test
  fun parse_entire_range_combos() {
    val json = """[{"c":"cc1","d":"dd1","e":"ee1"},{"c":"cc2","d":"dd2","e":"ee2"}]"""
    Json.parseToJsonElement(json).resolveAsType<JsonElement>("$[:]")
      .toString() shouldBe """[{"c":"cc1","d":"dd1","e":"ee1"},{"c":"cc2","d":"dd2","e":"ee2"}]"""
    Json.parseToJsonElement(json).resolveAsType<JsonElement>("$[:]['c']")
      .toString() shouldBe """["cc1","cc2"]"""
    Json.parseToJsonElement(json).resolveAsType<JsonElement>("$[:]['c','d']")
      .toString() shouldBe """["cc1","dd1","cc2","dd2"]"""
    Json.parseToJsonElement(json).resolveAsType<JsonElement>("$..[:]")
      .toString() shouldBe """[{"c":"cc1","d":"dd1","e":"ee1"},{"c":"cc2","d":"dd2","e":"ee2"}]"""
    Json.parseToJsonElement(json).resolveAsType<JsonElement>("$.*[:]").toString() shouldBe """[]"""

    val json2 = "[1,[2],[3,4],[5,6,7]]"
    Json.parseToJsonElement(json2).resolveAsType<JsonElement>("$[:]")
      .toString() shouldBe """[1,[2],[3,4],[5,6,7]]"""
    Json.parseToJsonElement(json2).resolveAsType<JsonElement>("$[:][0]").toString() shouldBe """[2,3,5]"""
    Json.parseToJsonElement(json2).resolveAsType<JsonElement>("$[:][1]").toString() shouldBe """[4,6]""" //
    Json.parseToJsonElement(json2).resolveAsType<JsonElement>("$.*[:]").toString() shouldBe """[2,3,4,5,6,7]"""
    Json.parseToJsonElement(json2).resolveAsType<JsonElement>("$..[:]")
      .toString() shouldBe """[1,[2],[3,4],[5,6,7],2,3,4,5,6,7]"""
    Json.parseToJsonElement(json2).resolveAsType<JsonElement>("$..[:].*")
      .toString() shouldBe """[2,3,4,5,6,7]"""
  }
}
