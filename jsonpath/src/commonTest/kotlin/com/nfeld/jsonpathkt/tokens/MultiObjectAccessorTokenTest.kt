package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.emptyJsonArray
import com.nfeld.jsonpathkt.readTree
import com.nfeld.jsonpathkt.toJsonNode
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test

class MultiObjectAccessorTokenTest {
  @Test
  fun should_return_empty_if_accessing_non_root_array_or_scalars() {
    // object accessor on an array should consistently return list
    MultiObjectAccessorToken(
      listOf(
        "a",
        "b",
      ),
    ).read(emptyJsonArray().toJsonNode()).element.toString() shouldBe "[]"
    MultiObjectAccessorToken(
      listOf(
        "a",
        "b",
      ),
    ).read(JsonPrimitive("yo").toJsonNode()).element.toString() shouldBe "[]"
    MultiObjectAccessorToken(
      listOf(
        "a",
        "b",
      ),
    ).read(JsonPrimitive(true).toJsonNode()).element.toString() shouldBe "[]"
  }

  @Test
  fun should_get_the_values_if_they_exist_in_object() {
    MultiObjectAccessorToken(
      listOf(
        "a",
        "b",
      ),
    ).read(readTree("""{"a":1,"b":2,"c":3}""").toJsonNode()).element.toString() shouldBe "[1,2]"
    MultiObjectAccessorToken(
      listOf(
        "a",
        "b",
      ),
    ).read(readTree("""{"a":1,"b":2}""").toJsonNode()).element.toString() shouldBe "[1,2]"
    MultiObjectAccessorToken(
      listOf(
        "a",
        "b",
      ),
    ).read(readTree("""{"a":1}""").toJsonNode()).element.toString() shouldBe "[1]"
  }

  @Test
  fun should_get_the_values_from_subcontainers_in_root_array_node() {
    MultiObjectAccessorToken(listOf("a", "b")).read(
      readTree("""[{"a":1,"b":2,"c":3}]""").toJsonNode(
        isNewRoot = true,
      ),
    ).element.toString() shouldBe "[1,2]"
    MultiObjectAccessorToken(
      listOf(
        "a",
        "b",
      ),
    ).read(readTree("""[{"a":1,"b":2}]""").toJsonNode(isNewRoot = true)).element.toString() shouldBe "[1,2]"
    MultiObjectAccessorToken(
      listOf(
        "a",
        "b",
      ),
    ).read(readTree("""[{"a":1}]""").toJsonNode(isNewRoot = true)).element.toString() shouldBe "[1]"
    MultiObjectAccessorToken(
      listOf(
        "a",
        "b",
      ),
    ).read(readTree("""[{}]""").toJsonNode(isNewRoot = true)).element.toString() shouldBe "[]"
    MultiObjectAccessorToken(
      listOf(
        "a",
        "b",
      ),
    ).read(readTree("""[]""").toJsonNode(isNewRoot = true)).element.toString() shouldBe "[]"
  }
}
