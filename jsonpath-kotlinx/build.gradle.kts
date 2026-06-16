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

  // androidNativeArm32 is excluded because kotest dropped support for it in 6.2.0
  androidNativeArm64()
  androidNativeX64()
  androidNativeX86()

  @OptIn(ExperimentalAbiValidation::class)
  abiValidation()

  sourceSets {
    commonMain.dependencies {
      api(projects.jsonpathCore)
      api(libs.kotlinx.serialization.json)
    }
  }
}
