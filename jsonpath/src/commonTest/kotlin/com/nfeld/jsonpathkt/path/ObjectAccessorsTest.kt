package com.nfeld.jsonpathkt.path

import com.nfeld.jsonpathkt.JsonPath
import com.nfeld.jsonpathkt.SMALL_JSON
import com.nfeld.jsonpathkt.extension.read
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonElement
import kotlin.test.Test

class ObjectAccessorsTest {
  @Test
  fun parse_should_be_null_if_key_does_not_exist() {
    JsonPath.parse(SMALL_JSON)!!.read<JsonElement>("$.unknownkey") shouldBe null
    JsonPath.parse(SMALL_JSON)!!.read<JsonElement>("$['unknownkey']") shouldBe null
  }

  @Test
  fun parse_should_get_value_if_key_exists() {
    JsonPath.parse(SMALL_JSON)!!.read<Int>("$.key") shouldBe 5
    JsonPath.parse(SMALL_JSON)!!.read<Int>("$['key']") shouldBe 5
  }

  @Test
  fun parse_should_be_null_if_reading_null_value() {
    JsonPath.parse("""{"key":null}""")!!.read<Int>("$['key']") shouldBe null
  }

  @Test
  fun parse_should_access_empty_string_key_and_other_uncommon_keys() {
    JsonPath.parse("""{"":4}""")!!.read<Int>("$['']") shouldBe 4
    JsonPath.parse("""{"":4}""")!!.read<Int>("$[\"\"]") shouldBe 4
    JsonPath.parse("""{"'":4}""")!!.read<Int>("$[\"'\"]") shouldBe 4
    JsonPath.parse("""{"'":4}""")!!.read<Int>("$['\\'']") shouldBe 4
    JsonPath.parse("""{"\"": 4}""")!!.read<Int>("""$["\""]""") shouldBe 4
    JsonPath.parse("""{"\"": 4}""")!!.read<Int>("""$['"']""") shouldBe 4
    JsonPath.parse("""{"\\": 4}""")!!.read<Int>("""$['\\']""") shouldBe 4
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
    JsonPath.parse(json)!!.read<String>("$.key1") shouldBe "a"
    JsonPath.parse(json)!!.read<String>("$['key1']") shouldBe "a"
    JsonPath.parse(json)!!.read<String>("$.ke2y") shouldBe "b"
    JsonPath.parse(json)!!.read<String>("$['ke2y']") shouldBe "b"
    JsonPath.parse(json)!!.read<String>("$.ke3%y") shouldBe "c"
    JsonPath.parse(json)!!.read<String>("$['ke3%y']") shouldBe "c"
    JsonPath.parse(json)!!.read<String>("$.1234") shouldBe "d"
    JsonPath.parse(json)!!.read<String>("$['1234']") shouldBe "d"
    JsonPath.parse(json)!!.read<String>("$.12$34") shouldBe "e"
    JsonPath.parse(json)!!.read<String>("$['12$34']") shouldBe "e"
    JsonPath.parse(json)!!.read<String>("$.abc{}3d") shouldBe "f"
    JsonPath.parse(json)!!.read<String>("$['abc{}3d']") shouldBe "f"
    JsonPath.parse(json)!!.read<String>("$['$key']") shouldBe "g"
  }

  @Test
  fun parse_should_be_null_on_unsupported_selectors_on_objects() {
    JsonPath.parse(SMALL_JSON)!!.read<Int>("$[:]") shouldBe null
  }

  @Test
  fun parse_should_read_key_from_list_if_list_item_is_an_object() {
    JsonPath.parse("""[{"key": "ey"}, {"key": "bee"}, {"key": "see"}]""")!!
      .read<JsonElement>("$.key") shouldBe null
    JsonPath.parse("""[{"key": "ey"}, {"key": "bee"}, {"key": "see"}]""")!!
      .read<JsonElement>("$.*.key")
      .toString() shouldBe """["ey","bee","see"]"""
    JsonPath.parse("""[{"key": "ey"}, {"key": "bee"}, {"key": "see"}]""")!!
      .read<JsonElement>("$[0,2].key")
      .toString() shouldBe """["ey","see"]"""
    JsonPath.parse(
      """
                {
                    "one": {"key": "value"},
                    "two": {"k": "v"},
                    "three": {"some": "more", "key": "other value"}
                }
            """,
    )!!.read<JsonElement>("$['one','three'].key")
      .toString() shouldBe """["value","other value"]"""

    JsonPath.parse("""[{"a": 1},{"a": 1}]""")!!.read<JsonElement>("$[*].a")
      .toString() shouldBe """[1,1]"""
    JsonPath.parse("""[{"a": 1},{"a": 1}]""")!!.read<JsonElement>("$.*.a")
      .toString() shouldBe """[1,1]"""
  }
}
