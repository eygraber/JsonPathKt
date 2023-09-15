package com.nfeld.jsonpathkt

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.spi.cache.CacheProvider
import com.jayway.jsonpath.spi.cache.NOOPCache
import com.jayway.jsonpath.spi.json.JacksonJsonProvider
import com.nfeld.jsonpathkt.kotlinx.resolvePath
import kotlinx.serialization.json.Json
import com.jayway.jsonpath.JsonPath as JaywayJsonPath

fun main() {
  JvmBenchmark()
}

object JvmBenchmark : Benchmark(
  printReadmeFormat = true,
) {
  // pre-parse json
  private val kotlinxJson = Json.parseToJsonElement(LARGE_JSON)

  private val jaywayContext = JaywayJsonPath.parse(
    LARGE_JSON,
    Configuration.defaultConfiguration().jsonProvider(JacksonJsonProvider()),
  )

  operator fun invoke() {
    CacheProvider.setCache(NOOPCache())

    runAllBenchmarks()
  }

  override fun pathResolveBenchmarks(): List<BenchmarkOp> = listOf(
    BenchmarkOp(name = "JsonPathKt", f = kotlinxJson::resolvePath),
    BenchmarkOp(name = "JsonPath", f = { path -> jaywayContext.read<Any>(path) }),
  )

  override fun pathCompilationBenchmarks(): List<BenchmarkOp> = listOf(
    BenchmarkOp(name = "JsonPathKt", f = JsonPath::compile),
    BenchmarkOp(name = "JsonPath", f = JaywayJsonPath::compile),
  )
}
