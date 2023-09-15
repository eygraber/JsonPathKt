package com.nfeld.jsonpathkt

import com.nfeld.jsonpathkt.tokens.ArrayAccessorToken
import com.nfeld.jsonpathkt.tokens.ArrayLengthBasedRangeAccessorToken
import com.nfeld.jsonpathkt.tokens.DeepScanArrayAccessorToken
import com.nfeld.jsonpathkt.tokens.DeepScanLengthBasedArrayAccessorToken
import com.nfeld.jsonpathkt.tokens.DeepScanObjectAccessorToken
import com.nfeld.jsonpathkt.tokens.DeepScanWildcardToken
import com.nfeld.jsonpathkt.tokens.MultiArrayAccessorToken
import com.nfeld.jsonpathkt.tokens.MultiObjectAccessorToken
import com.nfeld.jsonpathkt.tokens.ObjectAccessorToken
import com.nfeld.jsonpathkt.tokens.WildcardToken
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PathCompilerTest {
  @Test
  fun compile() {
    val f = PathCompiler::compile

    assertEquals(
      listOf(
        ArrayAccessorToken(2),
        DeepScanObjectAccessorToken(listOf("name", "id")),
      ),
      f("$[2]..['name','id']"),
    )
    assertEquals(
      listOf(
        ArrayAccessorToken(2),
        DeepScanObjectAccessorToken(listOf("name", "id")),
        ArrayAccessorToken(2),
      ),
      f("$[2]..['name','id'][2]"),
    )

    assertEquals(listOf(DeepScanObjectAccessorToken(listOf("name"))), f("$..['name']"))
    assertEquals(
      listOf(DeepScanObjectAccessorToken(listOf("name", "age"))),
      f("$..['name','age']"),
    )
    assertEquals(listOf(DeepScanArrayAccessorToken(listOf(0))), f("$..[0]"))
    assertEquals(listOf(DeepScanArrayAccessorToken(listOf(0, 1, 6))), f("$..[0,1,6]"))
    assertEquals(listOf(DeepScanArrayAccessorToken(listOf(0, -1, -6))), f("$..[0,-1,-6]"))
    assertEquals(listOf(DeepScanArrayAccessorToken(listOf(-2))), f("$..[-2]"))
    assertEquals(listOf(DeepScanArrayAccessorToken(listOf(0, 1, 2))), f("$..[0:3]"))
    assertEquals(listOf(DeepScanArrayAccessorToken(listOf(0, 1, 2))), f("$..[:3]"))
    assertEquals(listOf(DeepScanLengthBasedArrayAccessorToken(1, null, 0)), f("$..[1:]"))
    assertEquals(listOf(DeepScanLengthBasedArrayAccessorToken(0, null, -2)), f("$..[:-2]"))
    assertEquals(listOf(DeepScanLengthBasedArrayAccessorToken(-5, null, 0)), f("$..[-5:]"))
    assertEquals(listOf(DeepScanLengthBasedArrayAccessorToken(0, null, 0)), f("$..[:]"))
    assertEquals(listOf(DeepScanLengthBasedArrayAccessorToken(0, null, -2)), f("$..[0:-2]"))
    assertEquals(listOf(DeepScanLengthBasedArrayAccessorToken(-5, 6, 0)), f("$..[-5:6]"))
    assertEquals(listOf(DeepScanLengthBasedArrayAccessorToken(-5, null, -2)), f("$..[-5:-2]"))
    assertEquals(listOf(ObjectAccessorToken("-")), f("$-"))
    assertEquals(listOf(ObjectAccessorToken("-0")), f("$-0"))
    assertEquals(listOf(ObjectAccessorToken("-"), ArrayAccessorToken(0)), f("$-[0]"))
    assertEquals(listOf(WildcardToken()), f("$.*"))
    assertEquals(listOf(WildcardToken(), ObjectAccessorToken("key")), f("$.*.key"))
    assertEquals(listOf(WildcardToken(), ArrayAccessorToken(3)), f("$.*[3]"))
    assertEquals(
      listOf(WildcardToken(), DeepScanObjectAccessorToken(listOf("key"))),
      f("$.*..key"),
    )
    assertEquals(
      listOf(WildcardToken(), DeepScanArrayAccessorToken(listOf(1, 2, 3))),
      f("$.*..[1:4]"),
    )
    f("""$..["key"]""") shouldBe listOf(DeepScanObjectAccessorToken(listOf("key")))
    f("$..*") shouldBe listOf(DeepScanWildcardToken())
    f("$..[*]") shouldBe listOf(DeepScanWildcardToken())
    f("$..*..*") shouldBe listOf(DeepScanWildcardToken(), DeepScanWildcardToken())
    f("$..[*]..[*]") shouldBe listOf(DeepScanWildcardToken(), DeepScanWildcardToken())
  }

  @Test
  fun should_compile_without_root_token() {
    val f = PathCompiler::compile

    assertEquals(listOf(ObjectAccessorToken("key")), f("key"))
    assertEquals(listOf(ObjectAccessorToken("key")), f("['key']"))
    assertEquals(listOf(ObjectAccessorToken("key")), f("""["key"]"""))
    assertEquals(listOf(ObjectAccessorToken("*")), f("*"))
    assertEquals(listOf(ObjectAccessorToken("key"), ArrayAccessorToken(4)), f("key[4]"))
    assertEquals(listOf(MultiObjectAccessorToken(listOf("a", "b"))), f("['a','b']"))
    assertEquals(listOf(MultiObjectAccessorToken(listOf("a", "b"))), f("""["a","b"]"""))
    assertEquals(listOf(ArrayAccessorToken(3)), f("[3]"))
    assertEquals(listOf(MultiArrayAccessorToken(listOf(3, 4))), f("[3,4]"))
    assertEquals(listOf(MultiArrayAccessorToken(listOf(0, 1, 2))), f("[:3]"))
    assertEquals(listOf(MultiArrayAccessorToken(listOf(0, 1, 2))), f("[0:3]"))
    assertEquals(listOf(ArrayLengthBasedRangeAccessorToken(1, null, 0)), f("[1:]"))
  }

  @Test
  fun should_find_matching_closing_bracket() {
    val start = 0
    val f = PathCompiler::findMatchingClosingBracket

    assertEquals(1, f("[]", start))
    assertEquals(2, f("[5]", start))
    assertEquals(3, f("[53]", start))
    assertEquals(4, f("['5']", start))
    assertEquals(3, f("[-5]", start))
    assertEquals(4, f("[-5:]", start))
    assertEquals(3, f("[:5]", start))
    assertEquals(4, f("[0:5]", start))
    assertEquals(2, f("[:]", start))
    assertEquals(6, f("[0,1,2]", start))
    assertEquals(5, f("['a[']", start))
    assertEquals(5, f("['a]']", start))
    assertEquals(7, f("['a\\'b']", start))
    assertEquals(9, f("['a\\'\\']']", start))
    assertEquals(6, f("['4\\a']", start))
    assertEquals(7, f("""["a\"b"]""", start))
    assertEquals(9, f("""["a\"\"]"]""", start))
    assertEquals(6, f("""["4\a"]""", start))
    assertEquals(2, f("[*]", start))
  }

  @Test
  fun compileBracket() {
    val f = PathCompiler::compileBracket
    val start = 1
    var end = 0

    fun findClosingIndex(path: String): String {
      println("Testing $path")
      end = PathCompiler.findMatchingClosingBracket(path, start)
      return path
    }

    assertEquals(ArrayAccessorToken(0), f(findClosingIndex("$[0]"), start, end))
    assertEquals(ArrayAccessorToken(-4), f(findClosingIndex("$[-4]"), start, end))
    assertEquals(
      MultiArrayAccessorToken(listOf(0, 1, 2)),
      f(findClosingIndex("$[:3]"), start, end),
    )
    assertEquals(
      ArrayLengthBasedRangeAccessorToken(3, null, 0),
      f(findClosingIndex("$[3:]"), start, end),
    )
    assertEquals(
      ArrayLengthBasedRangeAccessorToken(0, null, 0),
      f(findClosingIndex("$[:]"), start, end),
    )
    assertEquals(
      MultiArrayAccessorToken(listOf(1, 2, 3)),
      f(findClosingIndex("$[1:4]"), start, end),
    )
    assertEquals(
      MultiArrayAccessorToken(listOf(1, 2, 3)),
      f(findClosingIndex("$[1,2,3]"), start, end),
    )
    assertEquals(
      MultiArrayAccessorToken(listOf(1, -2, 3)),
      f(findClosingIndex("$[1,-2,3]"), start, end),
    )
    assertEquals(ObjectAccessorToken("name"), f(findClosingIndex("$['name']"), start, end))
    assertEquals(ObjectAccessorToken("4"), f(findClosingIndex("$['4']"), start, end))
    assertEquals(
      MultiObjectAccessorToken(listOf("name", "age")),
      f(findClosingIndex("$['name','age']"), start, end),
    )
    assertEquals(
      MultiObjectAccessorToken(listOf("name", "age", "4")),
      f(findClosingIndex("$['name','age',4]"), start, end),
    )
    assertEquals(
      ObjectAccessorToken("name:age"),
      f(findClosingIndex("$['name:age']"), start, end),
    )
    assertEquals(WildcardToken(), f(findClosingIndex("$[*]"), start, end))
    assertEquals(
      ObjectAccessorToken(""":@."$,*'\"""),
      f(findClosingIndex("""$[':@."$,*\'\\']"""), start, end),
    )
    assertEquals(ObjectAccessorToken(""), f(findClosingIndex("$['']"), start, end))
    assertEquals(ObjectAccessorToken(""), f(findClosingIndex("$[\"\"]"), start, end))
    assertEquals(ObjectAccessorToken("\\"), f(findClosingIndex("$['\\\\']"), start, end))
    assertEquals(ObjectAccessorToken("'"), f(findClosingIndex("$['\\'']"), start, end))
    assertEquals(ObjectAccessorToken("'"), f(findClosingIndex("$[\"'\"]"), start, end))
    assertEquals(ObjectAccessorToken("\""), f(findClosingIndex("$['\"']"), start, end))
    assertEquals(ObjectAccessorToken("\""), f(findClosingIndex("""$["\""]"""), start, end))

    // handle negative values in array ranges
    assertEquals(
      ArrayLengthBasedRangeAccessorToken(0, null, -1),
      f(findClosingIndex("$[:-1]"), start, end),
    )
    assertEquals(
      ArrayLengthBasedRangeAccessorToken(0, null, -3),
      f(findClosingIndex("$[:-3]"), start, end),
    )
    assertEquals(
      ArrayLengthBasedRangeAccessorToken(-1, null, 0),
      f(findClosingIndex("$[-1:]"), start, end),
    )
    assertEquals(
      ArrayLengthBasedRangeAccessorToken(-5, null, 0),
      f(findClosingIndex("$[-5:]"), start, end),
    )
    assertEquals(
      ArrayLengthBasedRangeAccessorToken(-5, null, -1),
      f(findClosingIndex("$[-5:-1]"), start, end),
    )
    assertEquals(
      ArrayLengthBasedRangeAccessorToken(5, null, -1),
      f(findClosingIndex("$[5:-1]"), start, end),
    )
    assertEquals(
      ArrayLengthBasedRangeAccessorToken(-5, 4, 0),
      f(findClosingIndex("$[-5:4]"), start, end),
    )

    // ignore space paddings
    assertEquals(ArrayAccessorToken(0), f(findClosingIndex("$[  0  ]"), start, end))
    assertEquals(
      MultiArrayAccessorToken(listOf(0, 3)),
      f(findClosingIndex("$[0,  3]"), start, end),
    )
    assertEquals(
      ArrayLengthBasedRangeAccessorToken(0, null, 0),
      f(findClosingIndex("$[ : ]"), start, end),
    )
    assertEquals(
      ArrayLengthBasedRangeAccessorToken(2, null, 0),
      f(findClosingIndex("$[ 2 : ]"), start, end),
    )
    assertEquals(
      MultiArrayAccessorToken(listOf(0, 1)),
      f(findClosingIndex("$[  : 2 ]"), start, end),
    )
    assertEquals(
      MultiArrayAccessorToken(listOf(1, 2)),
      f(findClosingIndex("$[ 1 : 3 ]"), start, end),
    )
    assertEquals(WildcardToken(), f(findClosingIndex("$[ *  ]"), start, end))
    assertEquals(ObjectAccessorToken("name"), f(findClosingIndex("$[  'name'  ]"), start, end))

    // double quotes should be identical to single quotes
    f(findClosingIndex("""$["key"]"""), start, end) shouldBe ObjectAccessorToken("key")
    f(findClosingIndex("""$["'key'"]"""), start, end) shouldBe ObjectAccessorToken("'key'")
    f(findClosingIndex("""$["ke'y"]"""), start, end) shouldBe ObjectAccessorToken("ke'y")
    f(findClosingIndex("""$["ke\"y"]"""), start, end) shouldBe ObjectAccessorToken("ke\"y")
    f(findClosingIndex("""$["key","key2"]"""), start, end) shouldBe MultiObjectAccessorToken(
      listOf("key", "key2"),
    )
  }

  @Test
  fun should_throw() {
    val compile = PathCompiler::compile
    val compileBracket = PathCompiler::compileBracket

    assertFailsWith<IllegalArgumentException> { compile("") } // path cannot be empty
    assertFailsWith<IllegalArgumentException> { compile("$[]") } // needs value in brackets
    assertFailsWith<IllegalArgumentException> { compile("$[") } // needs closing bracket
    assertFailsWith<IllegalArgumentException> { compile("$[[]") } // invalid char at end
    assertFailsWith<IllegalArgumentException> { compile("$[[]]") }
    assertFailsWith<IllegalArgumentException> { compile("$[[0]]") }
    assertFailsWith<IllegalArgumentException> { compile("$[0[0]]") }
    assertFailsWith<IllegalArgumentException> {
      compileBracket(
        "$[]",
        1,
        2,
      )
    } // no token returned
    assertFailsWith<IllegalArgumentException> { compile("$.") } // needs closing bracket
    assertFailsWith<IllegalArgumentException> { compile("$['\\") } // unexpected escape char
    assertFailsWith<IllegalArgumentException> {
      PathCompiler.findMatchingClosingBracket(
        "$['4\\",
        1,
      )
    }
    assertFailsWith<IllegalArgumentException> { compile("$[-'0']") } // cant use both negative and object accessor
    assertFailsWith<IllegalArgumentException> { compile("$-[]") }
    assertFailsWith<IllegalArgumentException> { compile("$['single'quote']") }
    assertFailsWith<IllegalArgumentException> { compile("$[*,1]") }
    assertFailsWith<IllegalArgumentException> { compile("""$["'key"']""") }
    assertFailsWith<IllegalArgumentException> { compile("""$['"key'"]""") }
    assertFailsWith<IllegalArgumentException> { compile("""['a'.'b']""") }
  }
}
