@file:Suppress("MissingPackageDeclaration")

import com.nfeld.jsonpathkt.Benchmark
import com.nfeld.jsonpathkt.BenchmarkOp
import com.nfeld.jsonpathkt.JsonPath
import com.nfeld.jsonpathkt.LARGE_JSON
import com.nfeld.jsonpathkt.kotlinx.resolvePathOrNull
import kotlinx.serialization.json.Json

fun main() {
  WasmJsBenchmark()
}

object WasmJsBenchmark : Benchmark() {
  // pre-parse json
  private val kotlinxJson = Json.parseToJsonElement(LARGE_JSON)

  override val targetName = "WasmJs"

  operator fun invoke() {
    runAllBenchmarks()
  }

  override fun pathResolveBenchmarks(): List<BenchmarkOp> = listOf(
    BenchmarkOp(name = "JsonPathKt", f = kotlinxJson::resolvePathOrNull),
  )

  override fun pathCompilationBenchmarks(): List<BenchmarkOp> = listOf(
    BenchmarkOp(name = "JsonPathKt", f = JsonPath.Companion::compile),
  )
}
