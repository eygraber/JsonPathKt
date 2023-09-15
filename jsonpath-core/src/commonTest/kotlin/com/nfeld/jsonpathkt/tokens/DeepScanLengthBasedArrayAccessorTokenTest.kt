package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.asJson
import com.nfeld.jsonpathkt.jsonNode
import com.nfeld.jsonpathkt.printTesting
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonArray
import kotlin.test.Test
import kotlin.test.assertEquals

class DeepScanLengthBasedArrayAccessorTokenTest {
  @Test
  fun should_handle_general_cases() {
    val json = "[0,1,2,3,4]".asJson as JsonArray

    printTesting("[0:]")
    var res = DeepScanLengthBasedArrayAccessorToken(
      0,
      null,
      0,
    ).read(json.jsonNode()).asJson.toString()
    assertEquals(json.toString(), res)

    printTesting("[1:]")
    res = DeepScanLengthBasedArrayAccessorToken(
      1,
      null,
      0,
    ).read(json.jsonNode()).asJson.toString()
    assertEquals("[1,2,3,4]", res)

    printTesting("[:-2]")
    res = DeepScanLengthBasedArrayAccessorToken(
      0,
      null,
      -2,
    ).read(json.jsonNode()).asJson.toString()
    assertEquals("[0,1,2]", res)

    printTesting("[-3:]")
    res = DeepScanLengthBasedArrayAccessorToken(
      -3,
      null,
      0,
    ).read(json.jsonNode()).asJson.toString()
    assertEquals("[2,3,4]", res)

    printTesting("[0:-2]")
    res = DeepScanLengthBasedArrayAccessorToken(
      0,
      null,
      -2,
    ).read(json.jsonNode()).asJson.toString()
    assertEquals("[0,1,2]", res)

    printTesting("[-4:3]")
    res = DeepScanLengthBasedArrayAccessorToken(
      -4,
      3,
      0,
    ).read(json.jsonNode()).asJson.toString()
    assertEquals("[1,2]", res)

    printTesting("[-3:-1]")
    res = DeepScanLengthBasedArrayAccessorToken(
      -3,
      null,
      -1,
    ).read(json.jsonNode()).asJson.toString()
    assertEquals("[2,3]", res)
  }

  @Test
  fun should_handle_different_levels_of_list_nesting() {
    DeepScanLengthBasedArrayAccessorToken(
      0,
      null,
      0,
    ).read("""[1,[2],[3,4],[5,6,7]]""".asJson.jsonNode()).asJson.toString() shouldBe "[1,[2],[3,4],[5,6,7],2,3,4,5,6,7]"
    DeepScanLengthBasedArrayAccessorToken(
      0,
      null,
      -1,
    ).read("""[1,[2],[3,4],[5,6,7]]""".asJson.jsonNode()).asJson.toString() shouldBe "[1,[2],[3,4],3,5,6]"
    DeepScanLengthBasedArrayAccessorToken(
      0,
      null,
      0,
    ).read(WildcardToken().read("""[1,[2],[3,4],[5,6,7]]""".asJson.jsonNode())).asJson.toString() shouldBe "[2,3,4,5,6,7]"
    DeepScanLengthBasedArrayAccessorToken(
      0,
      null,
      -1,
    ).read(WildcardToken().read("""[1,[2],[3,4],[5,6,7]]""".asJson.jsonNode())).asJson.toString() shouldBe "[3,5,6]"
    DeepScanLengthBasedArrayAccessorToken(
      0,
      null,
      0,
    ).read(WildcardToken().read("""[1,[2],[3,4],[5,6,7,[8,9,10,11]]]""".asJson.jsonNode())).asJson.toString() shouldBe "[2,3,4,5,6,7,[8,9,10,11],8,9,10,11]"
  }
}
