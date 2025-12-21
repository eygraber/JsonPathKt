import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-detekt2")
}

kotlin {
  kmpTargets(
    KmpTarget.Js,
    KmpTarget.Jvm,
    KmpTarget.Linux,
    KmpTarget.Macos,
    KmpTarget.Mingw,
    KmpTarget.WasmJs,
    project = project,
    binaryType = BinaryType.Executable,
    ignoreDefaultTargets = true,
  )

  jvm {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    mainRun {
      mainClass = "com.nfeld.jsonpathkt.JvmBenchmarkKt"
    }
  }

  sourceSets {
    commonMain.dependencies {
      implementation(projects.jsonpathCore)
      implementation(projects.jsonpathKotlinx)
      implementation(projects.jsonpathTest)
    }

    jvmMain.dependencies {
      implementation(projects.jsonpathJsonjava)
      implementation(libs.jackson.core)
      implementation(libs.jackson.databind)
      implementation(libs.jackson.moduleKotlin)
      implementation(libs.jayway.jsonPath)
      implementation(libs.slf4j)
    }
  }
}

gradleConventions {
  kotlin {
    explicitApiMode = ExplicitApiMode.Disabled
  }
}
