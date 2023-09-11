package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.readTree
import com.nfeld.jsonpathkt.toJsonNode
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ObjectAccessorTokenTest {
  private val objJson = readTree("""{"key":1}""")
  private val arrJson = readTree("""[{"key":1}]""")

  @Test
  fun should_get_value_from_key_if_it_exists() {
    ObjectAccessorToken("key").read(objJson.toJsonNode())?.element.toString() shouldBe "1"
  }

  @Test
  fun should_be_null_if_key_does_not_exist() {
    ObjectAccessorToken("missing").read(objJson.toJsonNode()) shouldBe null
  }

  @Test
  fun should_be_null_if_node_is_an_ArrayNode() {
    ObjectAccessorToken("key").read(arrJson.toJsonNode()) shouldBe null
  }

  @Test
  fun should_get_value_from_key_if_node_is_a_New_Root() {
    val rootJson = WildcardToken().read(arrJson.toJsonNode()) // should not be null
    ObjectAccessorToken("key").read(rootJson)?.element.toString() shouldBe "[1]" // list since it was root level
  }
}
