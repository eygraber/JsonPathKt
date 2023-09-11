package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.emptyJsonObject
import com.nfeld.jsonpathkt.readTree
import com.nfeld.jsonpathkt.toJsonNode
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonArray
import kotlin.test.Test

class ArrayAccessorTokenTest {
  @Test
  fun should_be_null_if_item_does_not_exist_at_index() {
    ArrayAccessorToken(0).read(emptyJsonObject().toJsonNode()) shouldBe null
  }

  @Test
  fun should_get_the_item_if_it_exists_at_index() {
    ArrayAccessorToken(0).read(readTree("[1,2]").toJsonNode())?.element.toString() shouldBe "1"
  }

  @Test
  fun should_get_the_item_if_it_exists_at_index_if_negative() {
    ArrayAccessorToken(-1).read(readTree("[1,2]").toJsonNode())?.element.toString() shouldBe "2"
  }

  @Test
  fun should_get_last_item() {
    ArrayAccessorToken(-1).read(readTree("[1,2]").toJsonNode())?.element.toString() shouldBe "2"
  }

  @Test
  fun should_be_null_if_node_is_an_JsonObject() {
    ArrayAccessorToken(0).read(readTree("""{"0":1}""").toJsonNode()) shouldBe null
  }

  @Test
  fun should_get_item_if_node_is_a_New_Root() {
    val rootJson = readTree("[[1]]") as JsonArray
    ArrayAccessorToken(0).read(rootJson.toJsonNode(isNewRoot = true))?.element.toString() shouldBe "[1]" // list since it was root level
  }

  @Test
  fun should_get_first_item_of_sublists_if_node_is_a_New_Root() {
    val rootJson = readTree("[1,[2],[3,4],[5,6,7]]") as JsonArray
    ArrayAccessorToken(0).read(rootJson.toJsonNode(isNewRoot = true))?.element.toString() shouldBe "[2,3,5]"
  }

  @Test
  fun should_get_last_item_of_sublists_if_node_is_a_New_Root() {
    val rootJson = readTree("[1,[2],[3,4],[5,6,7]]") as JsonArray
    ArrayAccessorToken(-1).read(rootJson.toJsonNode(isNewRoot = true))?.element.toString() shouldBe "[2,4,7]"
  }

  @Test
  fun should_get_character_of_a_String_at_specified_index() {
    ArrayAccessorToken(1).read(readTree("\"hello\"").toJsonNode())?.element.toString() shouldBe "\"e\""
    ArrayAccessorToken(-1).read(readTree("\"hello\"").toJsonNode())?.element.toString() shouldBe "\"o\""
    ArrayAccessorToken(-8).read(readTree("\"hello\"").toJsonNode()) shouldBe null // out of bounds
  }

  @Test
  fun should_get_specified_character_of_every_String_in_a_root_level_array() {
    ArrayAccessorToken(1).read(WildcardToken().read(readTree("""["hello","world"]""").toJsonNode()))?.element.toString() shouldBe """["e","o"]"""
    ArrayAccessorToken(-1).read(WildcardToken().read(readTree("""["hello","world"]""").toJsonNode()))?.element.toString() shouldBe """["o","d"]"""
    ArrayAccessorToken(-4).read(WildcardToken().read(readTree("""["h","world"]""").toJsonNode()))?.element.toString() shouldBe """["o"]"""
  }
}
