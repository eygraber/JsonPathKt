package com.nfeld.jsonpathkt.path

import com.nfeld.jsonpathkt.JsonPath
import com.nfeld.jsonpathkt.LARGE_JSON
import com.nfeld.jsonpathkt.extension.read
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test

class ListCollectionsTest {
  @Test
  fun parse_should_include_nulls() {
    JsonPath.parse("""{"key": [1, "random", null, 1.765]}""")!!
      .read<List<JsonPrimitive?>>("$.key") shouldBe listOf(
      JsonPrimitive(1),
      JsonPrimitive("random"),
      null,
      JsonPrimitive(1.765),
    )
  }

  @Test
  fun parse_should_be_String_collection() {
    JsonPath.parse(LARGE_JSON)!!.read<List<String>>("$[0].tags") shouldBe listOf(
      "occaecat",
      "mollit",
      "ullamco",
      "labore",
      "cillum",
      "laboris",
      "qui",
    )
  }

  @Test
  fun parse_should_be_Int_collection() {
    JsonPath.parse(LARGE_JSON)!!.read<List<Int>>("$[5].nums") shouldBe listOf(1, 2, 3, 4, 5)
  }

  @Test
  fun parse_should_be_Long_collection() {
    JsonPath.parse(LARGE_JSON)!!.read<List<Long>>("$[5].nums") shouldBe listOf(
      1L,
      2L,
      3L,
      4L,
      5L,
    )
  }

  @Test
  fun parse_should_get_a_Set_collection_to_remove_duplicates() {
    JsonPath.parse("""[1,2,3,1,2,4,5]""")!!.read<Set<Int>>("$") shouldBe setOf(1, 2, 3, 4, 5)
  }
}
