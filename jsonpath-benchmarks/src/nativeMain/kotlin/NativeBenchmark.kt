import com.nfeld.jsonpathkt.Benchmark
import com.nfeld.jsonpathkt.BenchmarkOp
import com.nfeld.jsonpathkt.JsonPath
import com.nfeld.jsonpathkt.LARGE_JSON
import com.nfeld.jsonpathkt.kotlinx.resolvePath
import kotlinx.serialization.json.Json

fun main() {
  NativeBenchmark()
}

object NativeBenchmark : Benchmark(
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
    BenchmarkOp(name = "JsonPathKt", f = JsonPath.Companion::compile),
  )
}
