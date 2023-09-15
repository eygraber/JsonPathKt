package com.nfeld.jsonpathkt.path

import com.nfeld.jsonpathkt.LARGE_PARSED_JSON
import com.nfeld.jsonpathkt.resolveAsType
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test

class MultiObjectAccessorsTest {
  @Test
  fun parse_should_get_list_of_scalars() {
    Json.parseToJsonElement("""{"key": "value", "another": "entry"}""")
      .resolveAsType<List<String>>("$['key','another']") shouldBe listOf("value", "entry")
  }

  @Test
  fun parse_should_return_empty_list_of_reading_from_a_list_that_is_not_root_list() {
    Json.parseToJsonElement("""[{"key": "value", "another": "entry"}]""")
      .resolveAsType<JsonElement>("$['key','another']")
      .toString() shouldBe "[]"
    Json.parseToJsonElement("""[{"key": "ey", "other": 1}, {"key": "bee"}, {"key": "see", "else": 3}]""")
      .resolveAsType<JsonElement>("$['key','other']").toString() shouldBe "[]"
  }

  @Test
  fun parse_should_read_obj_keys_from_root_list() {
    Json.parseToJsonElement("""[{"key": "value", "another": "entry"}]""")
      .resolveAsType<List<String>>("$.*['key','another']") shouldBe listOf("value", "entry")
    Json.parseToJsonElement("""[{"key": "ey", "other": 1}, {"key": "bee"}, {"key": "see", "else": 3}]""")
      .resolveAsType<JsonElement>("$.*['key','other']").toString() shouldBe """["ey",1,"bee","see"]"""
  }

  @Test
  fun parse_should_get_all_3_keys() {
    LARGE_PARSED_JSON
      .resolveAsType<List<JsonElement>>("$[0]['latitude','longitude','isActive']") shouldBe listOf(
      JsonPrimitive(-85.888651), JsonPrimitive(38.287152), JsonPrimitive(true),
    )
  }

  @Test
  fun parse_should_get_only_the_key_value_pairs_when_found() {
    LARGE_PARSED_JSON
      .resolveAsType<List<Double>>("$[0]['latitude','longitude', 'unknownkey']") shouldBe listOf(
      -85.888651,
      38.287152,
    )
  }
}
