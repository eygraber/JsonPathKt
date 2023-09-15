package com.nfeld.jsonpathkt.tokens

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

class DeepScanWildcardTokenTest {
  @Test
  fun should_override_toString_hashCode_and_equals() {
    DeepScanWildcardToken().toString() shouldBe "DeepScanWildcardToken"
    DeepScanWildcardToken().hashCode() shouldBe "DeepScanWildcardToken".hashCode()
    DeepScanWildcardToken() shouldBe DeepScanWildcardToken()
    DeepScanWildcardToken() shouldNotBe ArrayAccessorToken(0)
  }
}
