package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.FAMILY_JSON
import com.nfeld.jsonpathkt.readTree
import com.nfeld.jsonpathkt.toJsonNode
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class DeepScanObjectAccessorTokenTest {
  @Test
  fun should_scan_for_keys() {
    DeepScanObjectAccessorToken(listOf("name")).read(readTree(FAMILY_JSON).toJsonNode()).element.toString() shouldBe """["Thomas","Mila","Konstantin","Tracy"]"""
    DeepScanObjectAccessorToken(listOf("nickname")).read(readTree(FAMILY_JSON).toJsonNode()).element.toString() shouldBe """["Kons"]"""
    DeepScanObjectAccessorToken(
      listOf(
        "name",
        "age",
      ),
    ).read(readTree(FAMILY_JSON).toJsonNode()).element.toString() shouldBe """["Thomas",13,"Mila",18,"Konstantin",29,"Tracy",4]"""
    DeepScanObjectAccessorToken(
      listOf(
        "name",
        "nickname",
      ),
    ).read(readTree(FAMILY_JSON).toJsonNode()).element.toString() shouldBe """["Thomas","Mila","Konstantin","Kons","Tracy"]"""
  }

  @Test
  fun results_should_be_a_New_Root() {
    DeepScanObjectAccessorToken(listOf("name")).read(readTree(FAMILY_JSON).toJsonNode()).isNewRoot shouldBe true
  }

  @Test
  fun should_handle_objects_on_different_levels() {
    val json = readTree("""[{"a":1},{"a":2,"b":3},{"a":4,"b":5,"c":{"a":6,"b":7,"c":8}}]""")
    DeepScanObjectAccessorToken(listOf("a")).read(json.toJsonNode()).element.toString() shouldBe """[1,2,4,6]"""
    DeepScanObjectAccessorToken(listOf("c")).read(json.toJsonNode()).element.toString() shouldBe """[{"a":6,"b":7,"c":8},8]"""
    DeepScanObjectAccessorToken(
      listOf(
        "a",
        "c",
      ),
    ).read(json.toJsonNode()).element.toString() shouldBe """[1,2,4,{"a":6,"b":7,"c":8},6,8]"""
    DeepScanObjectAccessorToken(listOf("a")).read(WildcardToken().read(json.toJsonNode())).element.toString() shouldBe """[1,2,4,6]"""
    DeepScanObjectAccessorToken(listOf("c")).read(WildcardToken().read(json.toJsonNode())).element.toString() shouldBe """[{"a":6,"b":7,"c":8},8]"""
    DeepScanObjectAccessorToken(
      listOf(
        "a",
        "c",
      ),
    ).read(WildcardToken().read(json.toJsonNode())).element.toString() shouldBe """[1,2,4,{"a":6,"b":7,"c":8},6,8]"""
  }
}
