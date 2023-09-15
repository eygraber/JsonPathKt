package com.nfeld.jsonpathkt.path

import com.nfeld.jsonpathkt.LARGE_PARSED_JSON
import com.nfeld.jsonpathkt.SMALL_JSON_ARRAY
import com.nfeld.jsonpathkt.resolveAsType
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlin.test.Test

class ArrayAccessorsTest {
  @Test
  fun parse_should_be_null_of_index_out_of_bounds() {
    Json.parseToJsonElement(SMALL_JSON_ARRAY).resolveAsType<JsonElement>("$[43]") shouldBe null
    Json.parseToJsonElement(SMALL_JSON_ARRAY).resolveAsType<JsonElement>("$[-43]") shouldBe null
  }

  @Test
  fun parse_should_get_value_if_value_exists_at_index() {
    Json.parseToJsonElement(SMALL_JSON_ARRAY).resolveAsType<Int>("$[2]") shouldBe 3
    Json.parseToJsonElement(SMALL_JSON_ARRAY).resolveAsType<Int>("$[0]") shouldBe 1
  }

  @Test
  fun parse_should_get_value_from_ends() {
    Json.parseToJsonElement(SMALL_JSON_ARRAY).resolveAsType<Int>("$[-2]") shouldBe 4
    Json.parseToJsonElement(SMALL_JSON_ARRAY).resolveAsType<Int>("$[-4]") shouldBe 2
    LARGE_PARSED_JSON.resolveAsType<String>("$[0]['tags'][-1]") shouldBe "qui"
    LARGE_PARSED_JSON.resolveAsType<String>("$[0]['tags'][-3]") shouldBe "cillum"
  }

  @Test
  fun parse_negative_0_should_get_first_item_in_array() {
    Json.parseToJsonElement(SMALL_JSON_ARRAY).resolveAsType<Int>("$[-0]") shouldBe 1
  }

  @Test
  fun parse_should_return_null_if_used_on_JSON_object() {
    Json.parseToJsonElement("""{"key":3}""").resolveAsType<JsonElement>("$[3]") shouldBe null
  }

  @Test
  fun parse_should_return_null_if_used_on_a_scalar_other_than_String() {
    Json.parseToJsonElement("5").resolveAsType<JsonElement>("$[0]") shouldBe null
    Json.parseToJsonElement("5.34").resolveAsType<JsonElement>("$[0]") shouldBe null
    Json.parseToJsonElement("true").resolveAsType<JsonElement>("$[0]") shouldBe null
    Json.parseToJsonElement("false").resolveAsType<JsonElement>("$[0]") shouldBe null
  }

  @Test
  fun parse_should_get_character_at_index_if_String_scalar() {
    Json.parseToJsonElement(""""hello"""").resolveAsType<String>("$[0]") shouldBe "h"
  }
}
