package com.nfeld.jsonpathkt.path

import com.nfeld.jsonpathkt.resolveAsType
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test

@Serializable
data class PojoClass1(val key: String)

@Serializable
data class PojoClass2(val key: List<JsonPrimitive?>)

@Serializable
data class PojoClass3(val key: Map<String, Int>, val default: Int)

class TypeCastingTest {
  @Test
  fun parse_should_cast_pojo() {
    Json.parseToJsonElement("""{"key": "value"}""").resolveAsType<PojoClass1>("$") shouldBe PojoClass1(key = "value")
    Json.parseToJsonElement("""{"key": [1, "random", null, 1.765]}""").resolveAsType<PojoClass2>("$") shouldBe PojoClass2(
      key = listOf(
        JsonPrimitive(1), JsonPrimitive("random"), null, JsonPrimitive(1.765),
      ),
    )
    Json.parseToJsonElement("""{"key": { "a": 1, "b": 2 }, "default": 3}""").resolveAsType<PojoClass3>("$") shouldBe PojoClass3(
      key = mapOf("a" to 1, "b" to 2),
      default = 3,
    )
  }
}
