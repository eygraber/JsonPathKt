package com.nfeld.jsonpathkt

import com.nfeld.jsonpathkt.kotlinx.resolvePathOrNull
import kotlinx.serialization.json.Json

fun main() {
  JsBenchmark()
}

object JsBenchmark : Benchmark() {
  // pre-parse json
  private val kotlinxJson = Json.parseToJsonElement(LARGE_JSON)

  override val targetName = "JS $platform"

  operator fun invoke() {
    runAllBenchmarks()
  }

  override fun pathResolveBenchmarks(): List<BenchmarkOp> = listOf(
    BenchmarkOp(name = "JsonPathKt", f = kotlinxJson::resolvePathOrNull),
  )

  override fun pathCompilationBenchmarks(): List<BenchmarkOp> = listOf(
    BenchmarkOp(name = "JsonPathKt", f = JsonPath::compile),
  )
}

private val platform: String
  get() =
    when {
      js("typeof window !== 'undefined'") == true -> "Browser"
      js("typeof process !== 'undefined'") == true -> "Node"
      else -> "Unknown"
    }
