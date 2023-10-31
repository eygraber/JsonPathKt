package com.nfeld.jsonpathkt.tokens

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

class DeepScanWildcardTokenTest {
  @Test
  fun should_be_a_data_object() {
    DeepScanWildcardToken.toString() shouldBe "DeepScanWildcardToken"
    DeepScanWildcardToken shouldBe DeepScanWildcardToken
    DeepScanWildcardToken shouldNotBe ArrayAccessorToken(0)
  }
}
