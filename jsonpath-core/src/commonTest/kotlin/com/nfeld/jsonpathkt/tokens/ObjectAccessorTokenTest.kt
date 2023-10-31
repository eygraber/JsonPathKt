package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.asJson
import com.nfeld.jsonpathkt.jsonNode
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ObjectAccessorTokenTest {
  private val objJson = """{"key":1}""".asJson
  private val arrJson = """[{"key":1}]""".asJson

  @Test
  fun should_get_value_from_key_if_it_exists() {
    ObjectAccessorToken("key").read(objJson.jsonNode())?.asJson.toString() shouldBe "1"
  }

  @Test
  fun should_be_null_if_key_does_not_exist() {
    ObjectAccessorToken("missing").read(objJson.jsonNode()) shouldBe null
  }

  @Test
  fun should_be_null_if_node_is_an_ArrayNode() {
    ObjectAccessorToken("key").read(arrJson.jsonNode()) shouldBe null
  }

  @Test
  fun should_get_value_from_key_if_node_is_a_New_Root() {
    val rootJson = WildcardToken.read(arrJson.jsonNode()) // should not be null
    ObjectAccessorToken("key").read(rootJson)?.asJson.toString() shouldBe "[1]" // list since it was root level
  }
}
