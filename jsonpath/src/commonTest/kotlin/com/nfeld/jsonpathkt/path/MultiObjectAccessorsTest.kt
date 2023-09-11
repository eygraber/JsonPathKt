package com.nfeld.jsonpathkt.path

import com.nfeld.jsonpathkt.JsonPath
import com.nfeld.jsonpathkt.LARGE_JSON
import com.nfeld.jsonpathkt.extension.read
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test

class MultiObjectAccessorsTest {
  @Test
  fun parse_should_get_list_of_scalars() {
    JsonPath.parse("""{"key": "value", "another": "entry"}""")!!
      .read<List<String>>("$['key','another']") shouldBe listOf("value", "entry")
  }

  @Test
  fun parse_should_return_empty_list_of_reading_from_a_list_that_is_not_root_list() {
    JsonPath.parse("""[{"key": "value", "another": "entry"}]""")!!
      .read<JsonElement>("$['key','another']")
      .toString() shouldBe "[]"
    JsonPath.parse("""[{"key": "ey", "other": 1}, {"key": "bee"}, {"key": "see", "else": 3}]""")!!
      .read<JsonElement>("$['key','other']").toString() shouldBe "[]"
  }

  @Test
  fun parse_should_read_obj_keys_from_root_list() {
    JsonPath.parse("""[{"key": "value", "another": "entry"}]""")!!
      .read<List<String>>("$.*['key','another']") shouldBe listOf("value", "entry")
    JsonPath.parse("""[{"key": "ey", "other": 1}, {"key": "bee"}, {"key": "see", "else": 3}]""")!!
      .read<JsonElement>("$.*['key','other']").toString() shouldBe """["ey",1,"bee","see"]"""
  }

  @Test
  fun parse_should_get_all_3_keys() {
    JsonPath.parse(LARGE_JSON)!!
      .read<List<JsonElement>>("$[0]['latitude','longitude','isActive']") shouldBe listOf(
      JsonPrimitive(-85.888651), JsonPrimitive(38.287152), JsonPrimitive(true),
    )
  }

  @Test
  fun parse_should_get_only_the_key_value_pairs_when_found() {
    JsonPath.parse(LARGE_JSON)!!
      .read<List<Double>>("$[0]['latitude','longitude', 'unknownkey']") shouldBe listOf(
      -85.888651,
      38.287152,
    )
  }
}
