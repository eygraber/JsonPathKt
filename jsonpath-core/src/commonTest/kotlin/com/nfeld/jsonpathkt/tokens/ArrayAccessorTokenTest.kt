package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.asJson
import com.nfeld.jsonpathkt.emptyJsonObject
import com.nfeld.jsonpathkt.jsonNode
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonArray
import kotlin.test.Test

class ArrayAccessorTokenTest {
  @Test
  fun should_be_null_if_item_does_not_exist_at_index() {
    ArrayAccessorToken(0).read(emptyJsonObject().jsonNode()) shouldBe null
  }

  @Test
  fun should_get_the_item_if_it_exists_at_index() {
    ArrayAccessorToken(0).read("[1,2]".asJson.jsonNode())?.asJson.toString() shouldBe "1"
  }

  @Test
  fun should_get_the_item_if_it_exists_at_index_if_negative() {
    ArrayAccessorToken(-1).read("[1,2]".asJson.jsonNode())?.asJson.toString() shouldBe "2"
  }

  @Test
  fun should_get_last_item() {
    ArrayAccessorToken(-1).read("[1,2]".asJson.jsonNode())?.asJson.toString() shouldBe "2"
  }

  @Test
  fun should_be_null_if_node_is_an_JsonObject() {
    ArrayAccessorToken(0).read("""{"0":1}""".asJson.jsonNode()) shouldBe null
  }

  @Test
  fun should_get_item_if_node_is_a_New_Root() {
    val rootJson = "[[1]]".asJson as JsonArray
    ArrayAccessorToken(0).read(rootJson.jsonNode(isWildcardScope = true))?.asJson.toString() shouldBe "[1]" // list since it was root level
  }

  @Test
  fun should_get_first_item_of_sublists_if_node_is_a_New_Root() {
    val rootJson = "[1,[2],[3,4],[5,6,7]]".asJson as JsonArray
    ArrayAccessorToken(0).read(rootJson.jsonNode(isWildcardScope = true))?.asJson.toString() shouldBe "[2,3,5]"
  }

  @Test
  fun should_get_last_item_of_sublists_if_node_is_a_New_Root() {
    val rootJson = "[1,[2],[3,4],[5,6,7]]".asJson as JsonArray
    ArrayAccessorToken(-1).read(rootJson.jsonNode(isWildcardScope = true))?.asJson.toString() shouldBe "[2,4,7]"
  }

  @Test
  fun should_get_character_of_a_String_at_specified_index() {
    ArrayAccessorToken(1).read("\"hello\"".asJson.jsonNode())?.asJson.toString() shouldBe "\"e\""
    ArrayAccessorToken(-1).read("\"hello\"".asJson.jsonNode())?.asJson.toString() shouldBe "\"o\""
    ArrayAccessorToken(-8).read("\"hello\"".asJson.jsonNode()) shouldBe null // out of bounds
  }

  @Test
  fun should_get_specified_character_of_every_String_in_a_root_level_array() {
    ArrayAccessorToken(1).read(WildcardToken.read("""["hello","world"]""".asJson.jsonNode()))?.asJson.toString() shouldBe """["e","o"]"""
    ArrayAccessorToken(-1).read(WildcardToken.read("""["hello","world"]""".asJson.jsonNode()))?.asJson.toString() shouldBe """["o","d"]"""
    ArrayAccessorToken(-4).read(WildcardToken.read("""["h","world"]""".asJson.jsonNode()))?.asJson.toString() shouldBe """["o"]"""
  }
}
