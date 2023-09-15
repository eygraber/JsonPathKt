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

abstract class Benchmark {
  abstract val targetName: String

  abstract fun pathResolveBenchmarks(): List<BenchmarkOp>

  abstract fun pathCompilationBenchmarks(): List<BenchmarkOp>

  protected fun runAllBenchmarks() {
    println("**$targetName**")
    printBenchmarkHeader(
      benchmarkName = "Path Tested ",
      impls = pathResolveBenchmarks().map { it.name },
    )

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

    println()

    println("**$targetName**")
    printBenchmarkHeader(
      benchmarkName = "Path Size",
      impls = pathCompilationBenchmarks().map { it.name },
    )
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

  private fun printBenchmarkHeader(
    benchmarkName: String,
    impls: List<String>,
  ) {
    val longestNameLength = impls.maxOf { it.length }

    val header = buildString {
      // LONGEST_LENGTH_BENCHMARK_PATH can change if new path are tested that are longer than the current one
      append("| $benchmarkName ${" ".repeat(LONGEST_LENGTH_BENCHMARK_PATH - benchmarkName.length)} |")

      impls.centerStrings().forEach { centeredName ->
        append("  $centeredName  |")
      }
    }

    val dashes = buildString {
      // add 2 for the preceding and trailing spaces
      append("|:${"-".repeat(LONGEST_LENGTH_BENCHMARK_PATH + 2)}|")

      repeat(impls.size) {
        // + 4 for added padding - 1 for : = + 3
        val dashes = "-".repeat(longestNameLength + 3)
        append(":$dashes|")
      }
    }

    println("$header\n$dashes")
  }

  private fun printResults(
    path: String,
    results: List<BenchmarkResult>,
  ) {
    println(
      buildString {
        // LONGEST_LENGTH_BENCHMARK_PATH can change if new path are tested that are longer than the current one
        append("| $path ${" ".repeat(LONGEST_LENGTH_BENCHMARK_PATH - path.length)} |")

        val longestNameLength = results.map { it.name }.maxOf { it.length }
        results.map { "${it.time.toString().padStart(4, ' ')} ms" }
          .centerStrings(longestLength = longestNameLength)
          .forEach { time ->
            append("  $time  |")
          }
      },
    )
  }

  companion object {
    // this is hardcoded to the length of $[0]['latitude','longitude','isActive']
    // update if needed
    private const val LONGEST_LENGTH_BENCHMARK_PATH = 39
  }
}

private fun List<String>.centerStrings(
  longestLength: Int = maxOf { it.length },
): List<String> = map { str ->
  val totalPadding = longestLength - str.length
  val leftPadding = (totalPadding + 1) / 2
  str.padStart(str.length + leftPadding).padEnd(longestLength)
}
