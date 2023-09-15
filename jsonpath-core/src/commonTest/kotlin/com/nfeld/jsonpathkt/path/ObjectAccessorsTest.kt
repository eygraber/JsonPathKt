package com.nfeld.jsonpathkt.path

import com.nfeld.jsonpathkt.SMALL_JSON
import com.nfeld.jsonpathkt.resolveAsType
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlin.test.Test

class ObjectAccessorsTest {
  @Test
  fun parse_should_be_null_if_key_does_not_exist() {
    Json.parseToJsonElement(SMALL_JSON).resolveAsType<JsonElement>("$.unknownkey") shouldBe null
    Json.parseToJsonElement(SMALL_JSON).resolveAsType<JsonElement>("$['unknownkey']") shouldBe null
  }

  @Test
  fun parse_should_get_value_if_key_exists() {
    Json.parseToJsonElement(SMALL_JSON).resolveAsType<Int>("$.key") shouldBe 5
    Json.parseToJsonElement(SMALL_JSON).resolveAsType<Int>("$['key']") shouldBe 5
  }

  @Test
  fun parse_should_be_null_if_reading_null_value() {
    Json.parseToJsonElement("""{"key":null}""").resolveAsType<Int>("$['key']") shouldBe null
  }

  @Test
  fun parse_should_access_empty_string_key_and_other_uncommon_keys() {
    Json.parseToJsonElement("""{"":4}""").resolveAsType<Int>("$['']") shouldBe 4
    Json.parseToJsonElement("""{"":4}""").resolveAsType<Int>("$[\"\"]") shouldBe 4
    Json.parseToJsonElement("""{"'":4}""").resolveAsType<Int>("$[\"'\"]") shouldBe 4
    Json.parseToJsonElement("""{"'":4}""").resolveAsType<Int>("$['\\'']") shouldBe 4
    Json.parseToJsonElement("""{"\"": 4}""").resolveAsType<Int>("""$["\""]""") shouldBe 4
    Json.parseToJsonElement("""{"\"": 4}""").resolveAsType<Int>("""$['"']""") shouldBe 4
    Json.parseToJsonElement("""{"\\": 4}""").resolveAsType<Int>("""$['\\']""") shouldBe 4
  }

  @Test
  fun parse_should_read_object_keys_that_have_numbers_and_or_symbols() {
    val key = "!@#\$%^&*()_-+=[]{}|:;<,>.?`~" // excluding '
    val json = """
                {
                    "key1": "a",
                    "ke2y": "b",
                    "ke3%y": "c",
                    "1234": "d",
                    "12$34": "e",
                    "abc{}3d": "f",
                    "$key": "g"
                }
            """
    Json.parseToJsonElement(json).resolveAsType<String>("$.key1") shouldBe "a"
    Json.parseToJsonElement(json).resolveAsType<String>("$['key1']") shouldBe "a"
    Json.parseToJsonElement(json).resolveAsType<String>("$.ke2y") shouldBe "b"
    Json.parseToJsonElement(json).resolveAsType<String>("$['ke2y']") shouldBe "b"
    Json.parseToJsonElement(json).resolveAsType<String>("$.ke3%y") shouldBe "c"
    Json.parseToJsonElement(json).resolveAsType<String>("$['ke3%y']") shouldBe "c"
    Json.parseToJsonElement(json).resolveAsType<String>("$.1234") shouldBe "d"
    Json.parseToJsonElement(json).resolveAsType<String>("$['1234']") shouldBe "d"
    Json.parseToJsonElement(json).resolveAsType<String>("$.12$34") shouldBe "e"
    Json.parseToJsonElement(json).resolveAsType<String>("$['12$34']") shouldBe "e"
    Json.parseToJsonElement(json).resolveAsType<String>("$.abc{}3d") shouldBe "f"
    Json.parseToJsonElement(json).resolveAsType<String>("$['abc{}3d']") shouldBe "f"
    Json.parseToJsonElement(json).resolveAsType<String>("$['$key']") shouldBe "g"
  }

  @Test
  fun parse_should_be_null_on_unsupported_selectors_on_objects() {
    Json.parseToJsonElement(SMALL_JSON).resolveAsType<Int>("$[:]") shouldBe null
  }

  @Test
  fun parse_should_read_key_from_list_if_list_item_is_an_object() {
    Json.parseToJsonElement("""[{"key": "ey"}, {"key": "bee"}, {"key": "see"}]""")
      .resolveAsType<JsonElement>("$.key") shouldBe null
    Json.parseToJsonElement("""[{"key": "ey"}, {"key": "bee"}, {"key": "see"}]""")
      .resolveAsType<JsonElement>("$.*.key")
      .toString() shouldBe """["ey","bee","see"]"""
    Json.parseToJsonElement("""[{"key": "ey"}, {"key": "bee"}, {"key": "see"}]""")
      .resolveAsType<JsonElement>("$[0,2].key")
      .toString() shouldBe """["ey","see"]"""
    Json.parseToJsonElement(
      """
                {
                    "one": {"key": "value"},
                    "two": {"k": "v"},
                    "three": {"some": "more", "key": "other value"}
                }
            """,
    ).resolveAsType<JsonElement>("$['one','three'].key")
      .toString() shouldBe """["value","other value"]"""

    Json.parseToJsonElement("""[{"a": 1},{"a": 1}]""").resolveAsType<JsonElement>("$[*].a")
      .toString() shouldBe """[1,1]"""
    Json.parseToJsonElement("""[{"a": 1},{"a": 1}]""").resolveAsType<JsonElement>("$.*.a")
      .toString() shouldBe """[1,1]"""
  }
}
