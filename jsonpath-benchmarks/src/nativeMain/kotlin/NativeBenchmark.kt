import com.nfeld.jsonpathkt.Benchmark
import com.nfeld.jsonpathkt.BenchmarkOp
import com.nfeld.jsonpathkt.JsonPath
import com.nfeld.jsonpathkt.LARGE_JSON
import com.nfeld.jsonpathkt.kotlinx.resolvePathOrNull
import kotlinx.serialization.json.Json
import kotlin.experimental.ExperimentalNativeApi

fun main() {
  NativeBenchmark()
}

object NativeBenchmark : Benchmark() {
  // pre-parse json
  private val kotlinxJson = Json.parseToJsonElement(LARGE_JSON)

  @OptIn(ExperimentalNativeApi::class)
  override val targetName = "Native (${Platform.osFamily.name}_${Platform.cpuArchitecture.name})"

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
