package com.nfeld.jsonpathkt

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.spi.cache.CacheProvider
import com.jayway.jsonpath.spi.cache.NOOPCache
import com.jayway.jsonpath.spi.json.JacksonJsonProvider
import com.nfeld.jsonpathkt.jsonjava.resolvePathOrNull
import com.nfeld.jsonpathkt.kotlinx.resolvePathOrNull
import kotlinx.serialization.json.Json
import org.json.JSONArray
import com.jayway.jsonpath.JsonPath as JaywayJsonPath

fun main() {
  JvmBenchmark()
}

object JvmBenchmark : Benchmark() {
  // pre-parse json
  private val kotlinxJson = Json.parseToJsonElement(LARGE_JSON)
  private val jsonOrgJson = JSONArray(LARGE_JSON)

  private val jaywayContext = JaywayJsonPath.parse(
    LARGE_JSON,
    Configuration.defaultConfiguration().jsonProvider(JacksonJsonProvider()),
  )

  override val targetName = "JVM"

  operator fun invoke() {
    CacheProvider.setCache(NOOPCache())

    runAllBenchmarks()
  }

  override fun pathResolveBenchmarks(): List<BenchmarkOp> = listOf(
    BenchmarkOp(name = "JsonPathKtKotlinx", f = kotlinxJson::resolvePathOrNull),
    BenchmarkOp(name = "JsonPathKtJsonJava", f = jsonOrgJson::resolvePathOrNull),
    BenchmarkOp(name = "JsonPath", f = { path -> jaywayContext.read<Any>(path) }),
  )

  override fun pathCompilationBenchmarks(): List<BenchmarkOp> = listOf(
    BenchmarkOp(name = "JsonPathKt", f = JsonPath::compile),
    BenchmarkOp(name = "JsonPath", f = JaywayJsonPath::compile),
  )
}
