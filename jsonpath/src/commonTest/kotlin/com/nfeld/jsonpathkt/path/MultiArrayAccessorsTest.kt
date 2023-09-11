package com.nfeld.jsonpathkt.path

import com.nfeld.jsonpathkt.JsonPath
import com.nfeld.jsonpathkt.LARGE_JSON
import com.nfeld.jsonpathkt.extension.read
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonElement
import kotlin.test.Test

class MultiArrayAccessorsTest {
  @Test
  fun parse_should_get_first_fourth_and_sixth_items() {
    JsonPath.parse(LARGE_JSON)!!.read<List<String>>("$[0]['tags'][0,3,5]") shouldBe listOf(
      "occaecat",
      "labore",
      "laboris",
    )
  }

  @Test
  fun parse_should_get_only_the_items_with_valid_index() {
    JsonPath.parse(LARGE_JSON)!!
      .read<List<String>>("$[0]['tags'][0,30,50]") shouldBe listOf("occaecat")
  }

  @Test
  fun parse_should_return_empty_list_if_used_on_JSON_object() {
    JsonPath.parse("""{"key":3}""")!!.read<JsonElement>("$[3,4]")?.toString() shouldBe "[]"
  }
}
