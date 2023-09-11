package com.nfeld.jsonpathkt.path

import com.nfeld.jsonpathkt.FAMILY_JSON
import com.nfeld.jsonpathkt.JsonPath
import com.nfeld.jsonpathkt.LARGE_JSON
import com.nfeld.jsonpathkt.extension.read
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test

class ArrayRangesTest {
  @Test
  fun parse_should_handle_array_range_from_start() {
    JsonPath.parse(LARGE_JSON)!!.read<List<String>>("$[0]['tags'][:3]") shouldBe listOf(
      "occaecat",
      "mollit",
      "ullamco",
    )
    JsonPath.parse(LARGE_JSON)!!.read<List<String>>("$[0]['tags'][:-4]") shouldBe listOf(
      "occaecat",
      "mollit",
      "ullamco",
    )
  }

  @Test
  fun parse_should_handle_array_range_to_end() {
    JsonPath.parse(LARGE_JSON)!!
      .read<List<String>>("$[0]['tags'][5:]") shouldBe listOf("laboris", "qui")
    JsonPath.parse(LARGE_JSON)!!
      .read<List<String>>("$[0]['tags'][-2:]") shouldBe listOf("laboris", "qui")
  }

  @Test
  fun parse_should_handle_specified_range_exclusive_at_end() {
    JsonPath.parse(LARGE_JSON)!!
      .read<List<String>>("$[0]['tags'][3:5]") shouldBe listOf("labore", "cillum")
    JsonPath.parse(LARGE_JSON)!!.read<List<String>>("$[0]['tags'][3:-1]") shouldBe listOf(
      "labore",
      "cillum",
      "laboris",
    )
    JsonPath.parse(LARGE_JSON)!!.read<List<String>>("$[0]['tags'][-6:4]") shouldBe listOf(
      "mollit",
      "ullamco",
      "labore",
    )
    JsonPath.parse(LARGE_JSON)!!.read<List<String>>("$[0]['tags'][-3:-1]") shouldBe listOf(
      "cillum",
      "laboris",
    )
  }

  @Test
  fun parse_should_return_range_items_up_to_end_if_end_index_out_of_bounds() {
    JsonPath.parse(LARGE_JSON)!!
      .read<List<String>>("$[0]['tags'][5:30]") shouldBe listOf("laboris", "qui")
  }

  @Test
  fun parse_should_return_range_items_up_from_start_if_start_index_out_of_bounds() {
    JsonPath.parse("""["first", "second", "third"]""")!!
      .read<List<String>>("$[-4:]") shouldBe listOf("first", "second", "third")
  }

  @Test
  fun parse_should_return_empty_list_if_used_on_JSON_object() {
    JsonPath.parse("""{"key":3}""")!!.read<JsonElement>("$[1:3]")?.toString() shouldBe "[]"
  }

  @Test
  fun parse_should_get_all_items_in_list() {
    JsonPath.parse("""["first", "second"]""")!!.read<List<String>>("$[:]") shouldBe listOf(
      "first",
      "second",
    )
    JsonPath.parse("""["first", "second"]""")!!.read<List<String>>("$[0:]") shouldBe listOf(
      "first",
      "second",
    )
    JsonPath.parse("""["first", "second"]""")!!.read<List<String>>("$") shouldBe listOf(
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
    JsonPath.parse(FAMILY_JSON)!!
      .read<List<Map<String, JsonPrimitive>>>("$.family.children[:]") shouldBe expected
    JsonPath.parse(FAMILY_JSON)!!
      .read<List<Map<String, JsonPrimitive>>>("$.family.children[0:]") shouldBe expected
  }

  @Test
  fun parse_entire_range_combos() {
    val json = """[{"c":"cc1","d":"dd1","e":"ee1"},{"c":"cc2","d":"dd2","e":"ee2"}]"""
    JsonPath.parse(json)!!.read<JsonElement>("$[:]")
      .toString() shouldBe """[{"c":"cc1","d":"dd1","e":"ee1"},{"c":"cc2","d":"dd2","e":"ee2"}]"""
    JsonPath.parse(json)!!.read<JsonElement>("$[:]['c']")
      .toString() shouldBe """["cc1","cc2"]"""
    JsonPath.parse(json)!!.read<JsonElement>("$[:]['c','d']")
      .toString() shouldBe """["cc1","dd1","cc2","dd2"]"""
    JsonPath.parse(json)!!.read<JsonElement>("$..[:]")
      .toString() shouldBe """[{"c":"cc1","d":"dd1","e":"ee1"},{"c":"cc2","d":"dd2","e":"ee2"}]"""
    JsonPath.parse(json)!!.read<JsonElement>("$.*[:]").toString() shouldBe """[]"""

    val json2 = "[1,[2],[3,4],[5,6,7]]"
    JsonPath.parse(json2)!!.read<JsonElement>("$[:]")
      .toString() shouldBe """[1,[2],[3,4],[5,6,7]]"""
    JsonPath.parse(json2)!!.read<JsonElement>("$[:][0]").toString() shouldBe """[2,3,5]"""
    JsonPath.parse(json2)!!.read<JsonElement>("$[:][1]").toString() shouldBe """[4,6]""" // !!!!
    JsonPath.parse(json2)!!.read<JsonElement>("$.*[:]").toString() shouldBe """[2,3,4,5,6,7]"""
    JsonPath.parse(json2)!!.read<JsonElement>("$..[:]")
      .toString() shouldBe """[1,[2],[3,4],[5,6,7],2,3,4,5,6,7]"""
    JsonPath.parse(json2)!!.read<JsonElement>("$..[:].*")
      .toString() shouldBe """[2,3,4,5,6,7]"""
  }
}
