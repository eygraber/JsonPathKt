import org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeTargetPreset
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
  java
  jacoco
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
  alias(libs.plugins.kotlinx.serialization)
}

kotlin {
  targets {
    kmpTargets(
      project = project,
      android = false,
      jvm = true,
      ios = true,
      macos = true,
      wasm = false,
      js = true,
    )

    presets.withType<AbstractKotlinNativeTargetPreset<*>>().forEach {
      if (!it.konanTarget.family.isAppleFamily && it.konanTarget !in KonanTarget.deprecatedTargets) {
        targetFromPreset(it)
      }
    }

    jvm {
      compilations.registerBenchmarkCompilation()
    }
  }

  sourceSets {
    commonMain {
      dependencies {
        implementation(libs.kotlinx.serialization.json)
      }
    }

    getByName("jvmBenchmark") {
      dependsOn(commonMain.get())
    }

    getByName("commonTest") {
      dependencies {
        implementation(libs.test.kotest.assertions)
        implementation(kotlin("test"))
      }
    }
  }
}

tasks.withType<Test> {
  testLogging {
    // show test results for following events
    events("PASSED", "FAILED", "SKIPPED")

    // show printlines
    showStandardStreams = true
  }

  addTestListener(
    object : TestListener {
      override fun beforeSuite(suite: TestDescriptor?) {}
      override fun afterTest(testDescriptor: TestDescriptor?, result: TestResult?) {}
      override fun beforeTest(testDescriptor: TestDescriptor?) {}

      override fun afterSuite(suite: TestDescriptor?, result: TestResult?) {
        val parent = suite?.parent
        if (parent != null && result != null) {
          println("\nTest result: ${result.resultType}")
          println(
            """
                        |Test summary: ${result.testCount} tests,
                        | ${result.successfulTestCount} succeeded,
                        | ${result.failedTestCount} failed,
                        | ${result.skippedTestCount} skipped
            """.trimMargin().replace("\n", ""),
          )
        }
      }
    },
  )

  configure<JacocoTaskExtension> {
    setDestinationFile(
      layout.buildDirectory.file("jacoco/junitPlatformTest.exec").map { it.asFile },
    )
    isIncludeNoLocationClasses = true
    excludes = listOf(
      "*/LRUCache\$LRUMap*",
      "*/JsonNodeKt*",
      "jdk.internal.*",
    )
  }

  // Make this task never up to date, thus forcing rerun of all tests whenever task is run
  outputs.upToDateWhen { false }
}

jacoco {
  toolVersion = "0.8.10"
  reportsDirectory.set(rootProject.layout.buildDirectory.dir("reports"))
}

tasks.jacocoTestReport.configure {
  dependsOn(tasks.named("jvmTest"))

  val buildDirPath = layout.buildDirectory.asFile.get().absolutePath

  val coverageSourceDirs = listOf("src/commonMain")
  val classFiles = File("$buildDirPath/classes/kotlin/jvm").walkBottomUp().toSet()

  classDirectories.setFrom(classFiles)
  sourceDirectories.setFrom(files(coverageSourceDirs))

  executionData.setFrom(files("$buildDirPath/jacoco/junitPlatformTest.exec"))

  reports {
    xml.required.set(true)
    html.required.set(true)
    csv.required.set(false)
  }
}

tasks.jacocoTestCoverageVerification {
  dependsOn(tasks.named("jvmTest"))
  violationRules {
    rule {
      limit {
        counter = "LINE"
        value = "COVEREDRATIO"
        minimum = BigDecimal(0.90)
      }
    }
  }
}

tasks.check.configure {
  dependsOn(tasks.jacocoTestReport)
  dependsOn(tasks.jacocoTestCoverageVerification)
}

fun NamedDomainObjectContainer<KotlinJvmCompilation>.registerBenchmarkCompilation() {
  val main = getByName("main")
  val test = getByName("test")
  create("benchmark") {
    defaultSourceSet {
      dependencies {
        implementation(main.compileDependencyFiles + main.output.classesDirs)
        implementation(test.compileDependencyFiles + test.output.classesDirs)

        implementation(libs.jayway.jsonPath)
        implementation(libs.json.org)
        implementation(libs.test.junit)
        implementation(libs.jackson.core)
        implementation(libs.jackson.databind)
        implementation(libs.jackson.moduleKotlin)
        implementation(libs.slf4j)
      }
    }

    // pass -PreadmeFormat to format benchmark results to update readme
    val readmeFormat = hasProperty("readmeFormat")

    tasks.register<Test>("benchmark") {
      classpath = compileDependencyFiles + runtimeDependencyFiles + output.allOutputs
      testClassesDirs = output.classesDirs

      useJUnitPlatform()

      if (readmeFormat) {
        jvmArgs("-DreadmeFormat=true")
      }

      testLogging {
        showStandardStreams = true
      }

      // Make this task never up to date, thus forcing rerun of all tests whenever task is run
      outputs.upToDateWhen { false }
    }
  }
}
