import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-detekt")
}

kotlin {

  kmpTargets(
    project = project,
    android = false,
    androidNative = false,
    jvm = true,
    ios = false,
    macos = true,
    linux = true,
    mingw = true,
    wasmJs = false,
    wasmWasi = false,
    js = true,
    binaryType = BinaryType.Executable,
  )

  jvm {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    mainRun {
      mainClass = "com.nfeld.jsonpathkt.JvmBenchmarkKt"
    }
  }

  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.jsonpathKotlinx)
        implementation(projects.jsonpathTest)
      }
    }

    jvmMain {
      dependencies {
        implementation(projects.jsonpathJsonjava)
        implementation(libs.jackson.core)
        implementation(libs.jackson.databind)
        implementation(libs.jackson.moduleKotlin)
        implementation(libs.jayway.jsonPath)
        implementation(libs.slf4j)
      }
    }
  }
}

gradleConventions {
  kotlin {
    explicitApiMode = ExplicitApiMode.Disabled
  }
}
