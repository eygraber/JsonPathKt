import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-detekt2")
  id("com.eygraber.conventions-publish-maven-central")
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
    commonMain.dependencies {
      api(projects.jsonpathCore)
      api(libs.kotlinx.serialization.json)
    }
  }
}
