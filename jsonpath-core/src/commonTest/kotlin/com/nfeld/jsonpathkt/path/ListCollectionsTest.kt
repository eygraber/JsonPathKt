package com.nfeld.jsonpathkt.path

import com.nfeld.jsonpathkt.LARGE_PARSED_JSON
import com.nfeld.jsonpathkt.resolveAsType
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test

class ListCollectionsTest {
  @Test
  fun parse_should_include_nulls() {
    Json.parseToJsonElement("""{"key": [1, "random", null, 1.765]}""")
      .resolveAsType<List<JsonPrimitive?>>("$.key") shouldBe listOf(
      JsonPrimitive(1),
      JsonPrimitive("random"),
      null,
      JsonPrimitive(1.765),
    )
  }

  @Test
  fun parse_should_be_String_collection() {
    LARGE_PARSED_JSON.resolveAsType<List<String>>("$[0].tags") shouldBe listOf(
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
    LARGE_PARSED_JSON.resolveAsType<List<Int>>("$[5].nums") shouldBe listOf(1, 2, 3, 4, 5)
  }

  @Test
  fun parse_should_be_Long_collection() {
    LARGE_PARSED_JSON.resolveAsType<List<Long>>("$[5].nums") shouldBe listOf(
      1L,
      2L,
      3L,
      4L,
      5L,
    )
  }

  @Test
  fun parse_should_get_a_Set_collection_to_remove_duplicates() {
    Json.parseToJsonElement("""[1,2,3,1,2,4,5]""").resolveAsType<Set<Int>>("$") shouldBe setOf(1, 2, 3, 4, 5)
  }
}
