package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.asJson
import com.nfeld.jsonpathkt.emptyJsonArray
import com.nfeld.jsonpathkt.emptyJsonObject
import com.nfeld.jsonpathkt.jsonNode
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

class WildcardTokenTest {
  @Test
  fun should_handle_empty_cases() {
    WildcardToken().read(emptyJsonArray().jsonNode()).asJson.toString() shouldBe """[]"""
    WildcardToken().read(emptyJsonObject().jsonNode()).asJson.toString() shouldBe """[]"""
  }

  @Test
  fun should_get_values_from_objects_and_strip() {
    val jsonObject = """{ "some": "string", "int": 42, "object": { "key": "value" }, "array": [0, 1] }""".asJson
    WildcardToken().read(jsonObject.jsonNode()).asJson.toString() shouldBe """["string",42,{"key":"value"},[0,1]]"""
  }

  @Test
  fun should_return_a_New_Root_if_root_list_replaced_with_another_list_before_modifying_values() {
    val jsonArray = """["string", 42, { "key": "value" }, [0, 1] ]""".asJson
    WildcardToken().read(jsonArray.jsonNode()).asJson.toString() shouldBe """["string",42,{"key":"value"},[0,1]]"""
  }

  @Test
  fun should_drop_scalars_and_move_everything_down_on_root_level_array() {
    val jsonArray = """["string", 42, { "key": "value" }, [0, 1] ]""".asJson
    val res1 = WildcardToken().read(jsonArray.jsonNode())
    res1.isWildcardScope shouldBe true
    val res2 = WildcardToken().read(res1)
    res2.asJson.toString() shouldBe """["value",0,1]"""
  }

  @Test
  fun should_override_toString_hashCode_and_equals() {
    WildcardToken().toString() shouldBe "WildcardToken"
    WildcardToken().hashCode() shouldBe "WildcardToken".hashCode()
    WildcardToken() shouldBe WildcardToken()
    WildcardToken() shouldNotBe ArrayAccessorToken(0)
  }
}
