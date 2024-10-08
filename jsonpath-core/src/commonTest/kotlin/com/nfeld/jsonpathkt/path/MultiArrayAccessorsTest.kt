package com.nfeld.jsonpathkt.path

import com.nfeld.jsonpathkt.LARGE_PARSED_JSON
import com.nfeld.jsonpathkt.resolveAsType
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlin.test.Test

class MultiArrayAccessorsTest {
  @Test
  fun parse_should_get_first_fourth_and_sixth_items() {
    LARGE_PARSED_JSON.resolveAsType<List<String>>("$[0]['tags'][0,3,5]") shouldBe listOf(
      "occaecat",
      "labore",
      "laboris",
    )
  }

  @Test
  fun parse_should_get_only_the_items_with_valid_index() {
    LARGE_PARSED_JSON
      .resolveAsType<List<String>>("$[0]['tags'][0,30,50]") shouldBe listOf("occaecat")
  }

  @Test
  fun parse_should_return_empty_list_if_used_on_JSON_object() {
    Json.parseToJsonElement("""{"key":3}""").resolveAsType<JsonElement>("$[3,4]")?.toString() shouldBe "[]"
  }

  @Test
  fun parse_should_resolve_ranges_before_unions() {
    val json = Json.parseToJsonElement("[1, 2, 3, 4]")
    json.resolveAsType<JsonElement>("$[:,0]")?.toString() shouldBe "[1,2,3,4,1]"
    json.resolveAsType<JsonElement>("$[0,:]")?.toString() shouldBe "[1,1,2,3,4]"
    json.resolveAsType<JsonElement>("$[0:0,0]")?.toString() shouldBe "[1,2,3,4,1]"
    json.resolveAsType<JsonElement>("$[0,0:0]")?.toString() shouldBe "[1,1,2,3,4]"
    json.resolveAsType<JsonElement>("$[:0,0]")?.toString() shouldBe "[1,2,3,4,1]"
    json.resolveAsType<JsonElement>("$[0:,0]")?.toString() shouldBe "[1,2,3,4,1]"
    json.resolveAsType<JsonElement>("$[0,-1:]")?.toString() shouldBe "[1,4]"
    json.resolveAsType<JsonElement>("$[0,:-1]")?.toString() shouldBe "[1,1,2,3]"
    json.resolveAsType<JsonElement>("$[:-1,:-1]")?.toString() shouldBe "[1,2,3,1,2,3]"
    json.resolveAsType<JsonElement>("$[:0,-1:]")?.toString() shouldBe "[1,2,3,4,4]"
    json.resolveAsType<JsonElement>("$[-1:,:-1]")?.toString() shouldBe "[4,1,2,3]"
    json.resolveAsType<JsonElement>("$[-1:,-1:]")?.toString() shouldBe "[4,4]"
  }
}
