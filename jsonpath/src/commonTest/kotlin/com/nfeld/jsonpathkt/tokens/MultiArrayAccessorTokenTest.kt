package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.emptyJsonObject
import com.nfeld.jsonpathkt.readTree
import com.nfeld.jsonpathkt.toJsonNode
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlin.test.Test
import kotlin.test.assertEquals

class MultiArrayAccessorTokenTest {
  @Test
  fun should_get_items_at_specified_indices() {
    MultiArrayAccessorToken(
      listOf(
        0,
        1,
      ),
    ).read(emptyJsonObject().toJsonNode()).element.toString() shouldBe "[]"

    val expected = buildJsonArray {
      add(JsonPrimitive(1))
      add(JsonPrimitive(3))
    }
    assertEquals(
      expected.toString(),
      MultiArrayAccessorToken(listOf(0, -1))
        .read(
          buildJsonArray {
            add(JsonPrimitive(1))
            add(JsonPrimitive(2))
            add(JsonPrimitive(3))
          }.toJsonNode(),
        )
        .element
        .toString(),
    )
  }

  @Test
  fun should_get_specified_items_of_sublists_if_node_is_a_New_Root() {
    val json = readTree("[1,[2],[3,4],[5,6,7]]") as JsonArray
    MultiArrayAccessorToken(
      listOf(
        0,
        1,
      ),
    ).read(json.toJsonNode(isNewRoot = true)).element.toString() shouldBe "[2,3,4,5,6]"
    MultiArrayAccessorToken(
      listOf(
        0,
        -1,
      ),
    ).read(json.toJsonNode(isNewRoot = true)).element.toString() shouldBe "[2,2,3,4,5,7]"
  }

  @Test
  fun should_be_able_to_get_same_index_multiple_times() {
    val json = readTree("[1,[2],[3,4],[5,6,7]]") as JsonArray
    MultiArrayAccessorToken(
      listOf(
        0,
        0,
        0,
      ),
    ).read(json.toJsonNode()).element.toString() shouldBe "[1,1,1]"
    MultiArrayAccessorToken(
      listOf(
        2,
        2,
      ),
    ).read(json.toJsonNode()).element.toString() shouldBe "[[3,4],[3,4]]"
    MultiArrayAccessorToken(
      listOf(
        0,
        0,
      ),
    ).read(json.toJsonNode(isNewRoot = true)).element.toString() shouldBe "[2,2,3,3,5,5]"
  }

  @Test
  fun should_get_characters_of_a_String_at_specified_indices() {
    MultiArrayAccessorToken(
      listOf(
        1,
        4,
      ),
    ).read(readTree("\"hello\"").toJsonNode()).element.toString() shouldBe """["e","o"]"""
  }

  @Test
  fun should_get_specified_characters_of_every_String_in_a_root_level_array() {
    MultiArrayAccessorToken(
      listOf(
        0,
        1,
      ),
    ).read(WildcardToken().read(readTree("""["hello","world"]""").toJsonNode())).element.toString() shouldBe """["h","e","w","o"]"""
  }
}
