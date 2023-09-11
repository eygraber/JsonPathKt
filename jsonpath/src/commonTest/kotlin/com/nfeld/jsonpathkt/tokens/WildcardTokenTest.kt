package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.emptyJsonArray
import com.nfeld.jsonpathkt.emptyJsonObject
import com.nfeld.jsonpathkt.readTree
import com.nfeld.jsonpathkt.toJsonNode
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

class WildcardTokenTest {
  @Test
  fun should_handle_empty_cases() {
    WildcardToken().read(emptyJsonArray().toJsonNode()).element.toString() shouldBe """[]"""
    WildcardToken().read(emptyJsonObject().toJsonNode()).element.toString() shouldBe """[]"""
  }

  @Test
  fun should_get_values_from_objects_and_strip() {
    val jsonObject =
      readTree("""{ "some": "string", "int": 42, "object": { "key": "value" }, "array": [0, 1] }""")
    WildcardToken().read(jsonObject.toJsonNode()).element.toString() shouldBe """["string",42,{"key":"value"},[0,1]]"""
  }

  @Test
  fun should_return_a_New_Root_if_root_list_replaced_with_another_list_before_modifying_values() {
    val jsonArray = readTree("""["string", 42, { "key": "value" }, [0, 1] ]""")
    WildcardToken().read(jsonArray.toJsonNode()).element.toString() shouldBe """["string",42,{"key":"value"},[0,1]]"""
  }

  @Test
  fun should_drop_scalars_and_move_everything_down_on_root_level_array() {
    val jsonArray = readTree("""["string", 42, { "key": "value" }, [0, 1] ]""")
    val res1 = WildcardToken().read(jsonArray.toJsonNode())
    res1.isNewRoot shouldBe true
    val res2 = WildcardToken().read(res1)
    res2.element.toString() shouldBe """["value",0,1]"""
  }

  @Test
  fun should_override_toString_hashCode_and_equals() {
    WildcardToken().toString() shouldBe "WildcardToken"
    WildcardToken().hashCode() shouldBe "WildcardToken".hashCode()
    WildcardToken() shouldBe WildcardToken()
    WildcardToken() shouldNotBe ArrayAccessorToken(0)
  }
}
