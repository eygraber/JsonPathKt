package com.nfeld.jsonpathkt

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.spi.json.JacksonJsonProvider
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import com.jayway.jsonpath.JsonPath as JaywayJsonPath
import com.jayway.jsonpath.spi.cache.CacheProvider as JaywayCacheProvider
import com.jayway.jsonpath.spi.cache.NOOPCache as JaywayNOOPCache

private const val DEFAULT_RUNS = 30
private const val DEFAULT_CALLS_PER_RUN = 80000
private var printReadmeFormat = false
private val timestamp: Long
  get() = System.currentTimeMillis()

private fun benchmark(
  callsPerRun: Int = DEFAULT_CALLS_PER_RUN,
  runs: Int = DEFAULT_RUNS,
  f: () -> Unit,
): Long {
  // warmup
  repeat(3) {
    f()
  }

  val times = mutableListOf<Long>()

  for (i in 0 until runs) {
    val t1 = timestamp
    for (k in 0 until callsPerRun) {
      f()
    }
    val t2 = timestamp
    times.add(t2 - t1)
  }

  return times.average().toLong()
}

private fun benchmarkJsonPathKt(
  path: String,
  callsPerRun: Int = DEFAULT_CALLS_PER_RUN,
  runs: Int = DEFAULT_RUNS,
): Long {
  val json = Json.parseToJsonElement(LARGE_JSON) // pre-parse json
  return benchmark(callsPerRun, runs) { json.read(path) }
}

private fun benchmarkJaywayJsonPath(
  path: String,
  callsPerRun: Int = DEFAULT_CALLS_PER_RUN,
  runs: Int = DEFAULT_RUNS,
): Long {
  val jaywayConfig = Configuration.defaultConfiguration().jsonProvider(JacksonJsonProvider())
  val documentContext = JaywayJsonPath.parse(LARGE_JSON, jaywayConfig)
  return benchmark(callsPerRun, runs) { documentContext.read<Any>(path) }
}

private fun runBenchmarksAndPrintResults(
  path: String,
  callsPerRun: Int = DEFAULT_CALLS_PER_RUN,
  runs: Int = DEFAULT_RUNS,
) {
  val kt = benchmarkJsonPathKt(path, callsPerRun, runs)
  val jayway = benchmarkJaywayJsonPath(path, callsPerRun, runs)

  if (printReadmeFormat) {
    println("|  $path  |  $kt ms |  $jayway ms |")
  } else {
    println("$path   kt: $kt, jsonpath: $jayway")
  }
}

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class BenchmarkTest {
  companion object {
    @BeforeAll
    @JvmStatic
    fun before() {
      println("Setting up BenchmarkTest")

      printReadmeFormat = System.getProperty("readmeFormat")?.toBoolean() ?: false

      JaywayCacheProvider.setCache(JaywayNOOPCache())
    }
  }

  @Test
  @Order(1)
  fun benchmarkDeepPath() {
    runBenchmarksAndPrintResults("$[0].friends[1].other.a.b['c']")
  }

  @Test
  @Order(2)
  fun benchmarkShallowPath() {
    runBenchmarksAndPrintResults("$[2]._id")
  }

  @Test
  @Order(3)
  fun benchmarkDeepScans() {
    val callsPerRun = 20000
    val runs = 10
    runBenchmarksAndPrintResults("$..name", callsPerRun, runs)
    runBenchmarksAndPrintResults("$..['email','name']", callsPerRun, runs)
    runBenchmarksAndPrintResults("$..[1]", callsPerRun, runs)
  }

  @Test
  @Order(4)
  fun benchmarkDeepScanRanges() {
    val callsPerRun = 20000
    val runs = 10
    runBenchmarksAndPrintResults("$..[:2]", callsPerRun, runs)
    runBenchmarksAndPrintResults("$..[2:]", callsPerRun, runs)

    // jayway jsonpath gives empty response for this so not valid comparison
    // runBenchmarksAndPrintResults("$..[1:-1]", callsPerRun, runs)
  }

  @Test
  @Order(5)
  fun benchmarkArrayAccessFromEndElement() {
    runBenchmarksAndPrintResults("$[0]['tags'][-3]")
  }

  @Test
  @Order(6)
  fun benchmarkArrayRangeFromStart() {
    runBenchmarksAndPrintResults("$[0]['tags'][:3]")
  }

  @Test
  @Order(7)
  fun benchmarkArrayRangeToEndElement() {
    runBenchmarksAndPrintResults("$[0]['tags'][3:]")
  }

  @Test
  @Order(8)
  fun benchmarkArrayRange() {
    runBenchmarksAndPrintResults("$[0]['tags'][3:5]")
  }

  @Test
  @Order(9)
  fun benchmarkMultiArrayAccess() {
    runBenchmarksAndPrintResults("$[0]['tags'][0,3,5]")
  }

  @Test
  @Order(10)
  fun benchmarkMultiObjectAccess() {
    runBenchmarksAndPrintResults("$[0]['latitude','longitude','isActive']")
  }

  @Test
  @Order(11)
  fun benchmarkWildcard() {
    runBenchmarksAndPrintResults("$[0]['tags'].*")
  }

  @Test
  @Order(12)
  fun benchmarkRecursiveWildcard() {
    runBenchmarksAndPrintResults("$[0]..*")
  }

  @Test
  @Order(13)
  fun benchmarkCompilingPath() {
    fun compile(path: String) {
      val kt = benchmark { PathCompiler.compile(path) }
      val jayway = benchmark { JaywayJsonPath.compile(path) }

      val numTokens = PathCompiler.compile(path).size
      val name = "${path.length} chars, $numTokens tokens"

      if (printReadmeFormat) {
        println("|  $name  |  $kt ms |  $jayway ms |")
      } else {
        println("$name  kt: $kt, jsonpath: $jayway")
      }
    }

    compile("$.hello")
    compile("$.hello.world[0]")
    compile("$[0].friends[1].other.a.b['c']")
    compile("$[0].friends[1].other.a.b['c'][5].niko[2].hello.world[6][9][0].id")
    compile("$[0].friends[1]..other[2].a.b['c'][5].niko[2]..hello[0].world[6][9]..['a','b','c'][0].id")
  }
}
