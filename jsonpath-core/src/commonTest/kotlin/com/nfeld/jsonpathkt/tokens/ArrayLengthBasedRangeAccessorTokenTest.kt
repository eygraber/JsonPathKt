package com.nfeld.jsonpathkt.tokens

import com.nfeld.jsonpathkt.asJson
import com.nfeld.jsonpathkt.emptyJsonObject
import com.nfeld.jsonpathkt.jsonNode
import com.nfeld.jsonpathkt.printTesting
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayLengthBasedRangeAccessorTokenTest {
  @Test
  fun should_return_empty_list() {
    ArrayLengthBasedRangeAccessorToken(0).read(emptyJsonObject().jsonNode()).asJson.toString() shouldBe "[]"
  }

  @Test
  fun should_not_get_characters_of_a_String() {
    ArrayLengthBasedRangeAccessorToken(1).read("\"hello\"".asJson.jsonNode()).asJson.toString() shouldBe "[]"
  }

  @Test
  fun should_not_get_characters_of_every_String_in_a_root_level_array() {
    ArrayLengthBasedRangeAccessorToken(
      0,
      2,
    ).read(WildcardToken().read("""["hello","world"]""".asJson.jsonNode())).asJson.toString() shouldBe "[]"
    ArrayLengthBasedRangeAccessorToken(
      2,
      null,
      -1,
    ).read(WildcardToken().read("""["hello","world"]""".asJson.jsonNode())).asJson.toString() shouldBe "[]"
  }

  @Test
  fun should_handle_objects_in_a_root_level_array() {
    ArrayLengthBasedRangeAccessorToken(
      0,
      1,
    ).read(WildcardToken().read("""[{"a":1,"b":{"c":2,"d":3},"e":4}]""".asJson.jsonNode())).asJson.toString() shouldBe "[]"
    ArrayLengthBasedRangeAccessorToken(
      0,
      -1,
    ).read(WildcardToken().read("""[{"a":1,"b":{"c":2,"d":3},"e":4}]""".asJson.jsonNode())).asJson.toString() shouldBe "[]"
    ArrayLengthBasedRangeAccessorToken(
      0,
      -1,
    ).read(WildcardToken().read("""[{"p":true},{"a":1,"b":{"c":2,"d":3},"e":4}]""".asJson.jsonNode())).asJson.toString() shouldBe "[]"
  }

  @Test
  fun should_handle_different_levels_of_list_nesting() {
    ArrayLengthBasedRangeAccessorToken(
      0,
      null,
      -1,
    ).read("""[1,[2],[3,4],[5,6,7]]""".asJson.jsonNode()).asJson.toString() shouldBe "[1,[2],[3,4]]"
    ArrayLengthBasedRangeAccessorToken(
      0,
      null,
      0,
    ).read("""[1,[2],[3,4],[5,6,7]]""".asJson.jsonNode()).asJson.toString() shouldBe "[1,[2],[3,4],[5,6,7]]"
    ArrayLengthBasedRangeAccessorToken(0).read(WildcardToken().read("""[1,[2],[3,4],[5,6,7]]""".asJson.jsonNode())).asJson.toString() shouldBe "[2,3,4,5,6,7]"
    ArrayLengthBasedRangeAccessorToken(
      0,
      null,
      -1,
    ).read(WildcardToken().read("""[1,[2],[3,4],[5,6,7]]""".asJson.jsonNode())).asJson.toString() shouldBe "[3,5,6]"
    ArrayLengthBasedRangeAccessorToken(
      0,
      null,
      0,
    ).read(WildcardToken().read("""[1,[2],[3,4],[5,6,7,[8,9,10,11]]]""".asJson.jsonNode())).asJson.toString() shouldBe "[2,3,4,5,6,7,[8,9,10,11]]"
  }

  @Test
  fun to_MultiArrayAccessorToken_general_cases() {
    val json = "[0,1,2,3,4]".asJson.jsonNode()

    printTesting("[0:]")
    var res = ArrayLengthBasedRangeAccessorToken(0, null, 0).toMultiArrayAccessorToken(json)
    var expected = MultiArrayAccessorToken(listOf(0, 1, 2, 3, 4))
    assertEquals(expected, res)

    printTesting("[3:]")
    res = ArrayLengthBasedRangeAccessorToken(3, null, 0).toMultiArrayAccessorToken(json)
    expected = MultiArrayAccessorToken(listOf(3, 4))
    assertEquals(expected, res)

    printTesting("[:-1]")
    res = ArrayLengthBasedRangeAccessorToken(0, null, -1).toMultiArrayAccessorToken(json)
    expected = MultiArrayAccessorToken(
      listOf(
        0,
        1,
        2,
        3,
      ),
    ) // this kind of range has end exclusive, so not really to end
    assertEquals(expected, res)

    // test starting edge
    printTesting("[:-4]")
    res = ArrayLengthBasedRangeAccessorToken(0, null, -4).toMultiArrayAccessorToken(json)
    expected = MultiArrayAccessorToken(listOf(0))
    assertEquals(expected, res)

    // test ending edge
    printTesting("[-1:]")
    res = ArrayLengthBasedRangeAccessorToken(-1, null, 0).toMultiArrayAccessorToken(json)
    expected = MultiArrayAccessorToken(listOf(4))
    assertEquals(expected, res)

    printTesting("[-2:]")
    res = ArrayLengthBasedRangeAccessorToken(-2, null, 0).toMultiArrayAccessorToken(json)
    expected = MultiArrayAccessorToken(listOf(3, 4))
    assertEquals(expected, res)

    printTesting("[-4:-1]")
    res = ArrayLengthBasedRangeAccessorToken(-4, null, -1).toMultiArrayAccessorToken(json)
    expected = MultiArrayAccessorToken(listOf(1, 2, 3))
    assertEquals(expected, res)

    printTesting("[-4:4]")
    res = ArrayLengthBasedRangeAccessorToken(-4, 4, 0).toMultiArrayAccessorToken(json)
    expected = MultiArrayAccessorToken(listOf(1, 2, 3))
    assertEquals(expected, res)

    printTesting("[2:-1]")
    res = ArrayLengthBasedRangeAccessorToken(2, null, -1).toMultiArrayAccessorToken(json)
    expected = MultiArrayAccessorToken(listOf(2, 3))
    assertEquals(expected, res)

    printTesting("[:]")
    res = ArrayLengthBasedRangeAccessorToken(0, null, 0).toMultiArrayAccessorToken(json)
    expected = MultiArrayAccessorToken(listOf(0, 1, 2, 3, 4))
    assertEquals(expected, res)
  }
}
