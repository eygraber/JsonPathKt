package com.nfeld.jsonpathkt

import com.nfeld.jsonpathkt.kotlinx.resolvePath
import kotlinx.serialization.json.Json

fun main() {
  JsBenchmark()
}

object JsBenchmark : Benchmark(
  printReadmeFormat = true,
) {
  // pre-parse json
  private val kotlinxJson = Json.parseToJsonElement(LARGE_JSON)

  operator fun invoke() {
    runAllBenchmarks()
  }

  override fun pathResolveBenchmarks(): List<BenchmarkOp> = listOf(
    BenchmarkOp(name = "JsonPathKt", f = kotlinxJson::resolvePath),
  )

  override fun pathCompilationBenchmarks(): List<BenchmarkOp> = listOf(
    BenchmarkOp(name = "JsonPathKt", f = JsonPath::compile),
  )
}
