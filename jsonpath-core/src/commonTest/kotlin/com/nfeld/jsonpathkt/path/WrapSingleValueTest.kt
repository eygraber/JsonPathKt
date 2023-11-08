package com.nfeld.jsonpathkt.path

import com.nfeld.jsonpathkt.BOOKS_JSON
import com.nfeld.jsonpathkt.LARGE_JSON
import com.nfeld.jsonpathkt.ResolutionOptions
import com.nfeld.jsonpathkt.SMALL_JSON
import com.nfeld.jsonpathkt.SMALL_JSON_ARRAY
import com.nfeld.jsonpathkt.asJson
import com.nfeld.jsonpathkt.kotlinx.resolvePathOrNull
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonElement
import kotlin.test.Test

class WrapSingleValueTest {
  @Test
  fun parse_should_wrap_root_accessor() {
    SMALL_JSON.resolvePathWrappedOrNull("$") shouldBe "[${SMALL_JSON}]".asJson
    SMALL_JSON_ARRAY.resolvePathWrappedOrNull("$") shouldBe "[${SMALL_JSON_ARRAY}]".asJson
  }

  @Test
  fun parse_should_wrap_array_accessor() {
    SMALL_JSON_ARRAY.resolvePathWrappedOrNull("$[0]") shouldBe "[1]".asJson
    SMALL_JSON_ARRAY.resolvePathWrappedOrNull("$[-1]") shouldBe "[${SMALL_JSON}]".asJson

    """
    |[
    |  [
    |    ["A"], ["B"], ["C"]
    |  ],
    |  [
    |    ["D"], ["E"], ["F"]
    |  ],
    |  [
    |    ["G"], ["H"], ["I"]
    |  ]
    |]
    """.trimMargin()
      .resolvePathWrappedOrNull("$.[1].[2]") shouldBe """[ [ "F" ] ]""".asJson
  }

  @Test
  fun parse_should_wrap_object_accessor() {
    SMALL_JSON.resolvePathWrappedOrNull("key") shouldBe "[5]".asJson
    SMALL_JSON_ARRAY.resolvePathWrappedOrNull("$[4].key") shouldBe "[5]".asJson
    LARGE_JSON.resolvePathWrappedOrNull("$[0].tags") shouldBe """[["occaecat","mollit","ullamco","labore","cillum","laboris","qui"]]""".asJson
  }

  @Test
  fun parse_should_not_wrap_wildcard_root_accessor() {
    SMALL_JSON_ARRAY.resolvePathWrappedOrNull("$*") shouldBe SMALL_JSON_ARRAY.asJson
    SMALL_JSON_ARRAY.resolvePathWrappedOrNull("$.*") shouldBe SMALL_JSON_ARRAY.asJson
  }

  @Test
  fun parse_should_not_wrap_wildcard_array_accessor() {
    """
    |[
    |  [
    |    ["A"], ["B"], ["C"]
    |  ],
    |  [
    |    ["D"], ["E"], ["F"]
    |  ],
    |  [
    |    ["G"], ["H"], ["I"]
    |  ]
    |]
    """.trimMargin()
      .resolvePathWrappedOrNull("$.[1].*.[0]") shouldBe """[ "D", "E", "F" ]""".asJson
  }

  @Test
  fun parse_should_not_wrap_wildcard_object_accessor() {
    BOOKS_JSON.resolvePathWrappedOrNull("$.store.book.*.author") shouldBe """["Nigel Rees","Evelyn Waugh","Herman Melville","J. R. R. Tolkien"]""".asJson
  }

  private val resolutionOptions = ResolutionOptions(wrapSingleValue = true)

  private fun String.resolvePathWrappedOrNull(path: String) =
    asJson.resolvePathWrappedOrNull(path)

  private fun JsonElement.resolvePathWrappedOrNull(path: String) =
    resolvePathOrNull(path, options = resolutionOptions)
}
