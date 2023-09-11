package com.nfeld.jsonpathkt.path

import com.nfeld.jsonpathkt.JsonPath
import com.nfeld.jsonpathkt.LARGE_JSON
import com.nfeld.jsonpathkt.SMALL_JSON_ARRAY
import com.nfeld.jsonpathkt.extension.read
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonElement
import kotlin.test.Test

class ArrayAccessorsTest {
  @Test
  fun parse_should_be_null_of_index_out_of_bounds() {
    JsonPath.parse(SMALL_JSON_ARRAY)!!.read<JsonElement>("$[43]") shouldBe null
    JsonPath.parse(SMALL_JSON_ARRAY)!!.read<JsonElement>("$[-43]") shouldBe null
  }

  @Test
  fun parse_should_get_value_if_value_exists_at_index() {
    JsonPath.parse(SMALL_JSON_ARRAY)!!.read<Int>("$[2]") shouldBe 3
    JsonPath.parse(SMALL_JSON_ARRAY)!!.read<Int>("$[0]") shouldBe 1
  }

  @Test
  fun parse_should_get_value_from_ends() {
    JsonPath.parse(SMALL_JSON_ARRAY)!!.read<Int>("$[-2]") shouldBe 4
    JsonPath.parse(SMALL_JSON_ARRAY)!!.read<Int>("$[-4]") shouldBe 2
    JsonPath.parse(LARGE_JSON)!!.read<String>("$[0]['tags'][-1]") shouldBe "qui"
    JsonPath.parse(LARGE_JSON)!!.read<String>("$[0]['tags'][-3]") shouldBe "cillum"
  }

  @Test
  fun parse_negative_0_should_get_first_item_in_array() {
    JsonPath.parse(SMALL_JSON_ARRAY)!!.read<Int>("$[-0]") shouldBe 1
  }

  @Test
  fun parse_should_return_null_if_used_on_JSON_object() {
    JsonPath.parse("""{"key":3}""")!!.read<JsonElement>("$[3]") shouldBe null
  }

  @Test
  fun parse_should_return_null_if_used_on_a_scalar_other_than_String() {
    JsonPath.parse("5")!!.read<JsonElement>("$[0]") shouldBe null
    JsonPath.parse("5.34")!!.read<JsonElement>("$[0]") shouldBe null
    JsonPath.parse("true")!!.read<JsonElement>("$[0]") shouldBe null
    JsonPath.parse("false")!!.read<JsonElement>("$[0]") shouldBe null
  }

  @Test
  fun parse_should_get_character_at_index_if_String_scalar() {
    JsonPath.parse(""""hello"""")!!.read<String>("$[0]") shouldBe "h"
  }
}
