package com.nfeld.jsonpathkt

import kotlin.time.measureTime

internal const val DEFAULT_WARMUP_RUNS = 3
internal const val DEFAULT_RUNS = 30
internal const val DEFAULT_CALLS_PER_RUN = 80_000

data class BenchmarkOp(
  val name: String,
  val f: (String) -> Unit,
)

data class BenchmarkResult(
  val name: String,
  val time: Long,
)

abstract class Benchmark(
  private val printReadmeFormat: Boolean,
) {
  abstract fun pathResolveBenchmarks(): List<BenchmarkOp>

  abstract fun pathCompilationBenchmarks(): List<BenchmarkOp>

  protected fun runAllBenchmarks() {
    benchmarkDeepPath()
    benchmarkShallowPath()
    benchmarkDeepScans()
    benchmarkDeepScanRanges()
    benchmarkArrayAccessFromEndElement()
    benchmarkArrayRangeFromStart()
    benchmarkArrayRangeToEndElement()
    benchmarkArrayRange()
    benchmarkMultiArrayAccess()
    benchmarkMultiObjectAccess()
    benchmarkWildcard()
    benchmarkRecursiveWildcard()
    benchmarkCompilingPath()
  }

  protected fun benchmarkDeepPath() {
    runBenchmarksAndPrintResults("$[0].friends[1].other.a.b['c']")
  }

  protected fun benchmarkShallowPath() {
    runBenchmarksAndPrintResults("$[2]._id")
  }

  protected fun benchmarkDeepScans() {
    val callsPerRun = 20_000
    val runs = 10
    runBenchmarksAndPrintResults("$..name", callsPerRun, runs)
    runBenchmarksAndPrintResults("$..['email','name']", callsPerRun, runs)
    runBenchmarksAndPrintResults("$..[1]", callsPerRun, runs)
  }

  protected fun benchmarkDeepScanRanges() {
    val callsPerRun = 20_000
    val runs = 10
    runBenchmarksAndPrintResults("$..[:2]", callsPerRun, runs)
    runBenchmarksAndPrintResults("$..[2:]", callsPerRun, runs)
    runBenchmarksAndPrintResults("$..[1:-1]", callsPerRun, runs)
  }

  protected fun benchmarkArrayAccessFromEndElement() {
    runBenchmarksAndPrintResults("$[0]['tags'][-3]")
  }

  protected fun benchmarkArrayRangeFromStart() {
    runBenchmarksAndPrintResults("$[0]['tags'][:3]")
  }

  protected fun benchmarkArrayRangeToEndElement() {
    runBenchmarksAndPrintResults("$[0]['tags'][3:]")
  }

  protected fun benchmarkArrayRange() {
    runBenchmarksAndPrintResults("$[0]['tags'][3:5]")
  }

  protected fun benchmarkMultiArrayAccess() {
    runBenchmarksAndPrintResults("$[0]['tags'][0,3,5]")
  }

  protected fun benchmarkMultiObjectAccess() {
    runBenchmarksAndPrintResults("$[0]['latitude','longitude','isActive']")
  }

  protected fun benchmarkWildcard() {
    runBenchmarksAndPrintResults("$[0]['tags'].*")
  }

  protected fun benchmarkRecursiveWildcard() {
    runBenchmarksAndPrintResults("$[0]..*")
  }

  private fun benchmarkCompilingPath() {
    fun compile(path: String) {
      val numTokens = JsonPath.compile(path).tokenCount
      val name = "${path.length} chars, $numTokens tokens"

      val results = pathCompilationBenchmarks().map { op ->
        BenchmarkResult(
          name = op.name,
          time = runBenchmark(
            f = { op.f(path) },
          ),
        )
      }

      printResults(
        path = name,
        results = results,
        printReadmeFormat = printReadmeFormat,
      )
    }

    compile("$.hello")
    compile("$.hello.world[0]")
    compile("$[0].friends[1].other.a.b['c']")
    compile("$[0].friends[1].other.a.b['c'][5].niko[2].hello.world[6][9][0].id")
    compile("$[0].friends[1]..other[2].a.b['c'][5].niko[2]..hello[0].world[6][9]..['a','b','c'][0].id")
  }

  private fun runBenchmarksAndPrintResults(
    path: String,
    callsPerRun: Int = DEFAULT_CALLS_PER_RUN,
    runs: Int = DEFAULT_RUNS,
  ) {
    val results = pathResolveBenchmarks().map { op ->
      BenchmarkResult(
        name = op.name,
        time = runBenchmark(
          callsPerRun = callsPerRun,
          runs = runs,
          f = { op.f(path) },
        ),
      )
    }
    printResults(
      path = path,
      results = results,
      printReadmeFormat = printReadmeFormat,
    )
  }

  private fun runBenchmark(
    warmupRuns: Int = DEFAULT_WARMUP_RUNS,
    callsPerRun: Int = DEFAULT_CALLS_PER_RUN,
    runs: Int = DEFAULT_RUNS,
    f: () -> Unit,
  ): Long {
    repeat(warmupRuns) {
      f()
    }

    val times = mutableListOf<Long>()

    repeat(runs) {
      measureTime {
        repeat(callsPerRun) {
          f()
        }
      }.let { times += it.inWholeMilliseconds }
    }

    return times.average().toLong()
  }

  private fun printResults(
    path: String,
    results: List<BenchmarkResult>,
    printReadmeFormat: Boolean,
  ) {
    println(
      buildString {
        if (printReadmeFormat) {
          append("|  $path  |")
          results.forEach { result ->
            append("  ${result.time} ms |")
          }
        } else {
          append("$path  ")
          append(
            results.joinToString { result ->
              "${result.name}: ${result.time} ms"
            },
          )
        }
      },
    )
  }
}
