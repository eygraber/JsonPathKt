import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-detekt2")
  id("com.eygraber.conventions-publish-maven-central")
  alias(libs.plugins.kotlinx.serialization)
}

kotlin {
  defaultKmpTargets(
    project = project,
  )

  @OptIn(ExperimentalAbiValidation::class)
  abiValidation {
    enabled.set(true)
    klib.enabled.set(true)
  }

  sourceSets {
    commonTest.dependencies {
      implementation(projects.jsonpathKotlinx) {
        // not having this results in duplicate classes
        exclude(mapOf("group" to "com.eygraber", "module" to "jsonpath-core"))
      }
      implementation(projects.jsonpathTest)

      implementation(libs.test.kotest.assertions)
      implementation(kotlin("test"))
    }
  }
}
