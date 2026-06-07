import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
  kotlin("jvm")
  id("com.eygraber.conventions-kotlin-library")
  id("com.eygraber.conventions-detekt2")
  id("com.eygraber.conventions-publish-maven-central")
}

kotlin {
  @OptIn(ExperimentalAbiValidation::class)
  abiValidation {
    enabled.set(true)
  }
}

dependencies {
  api(projects.jsonpathCore)
  api(libs.jsonJava)
}
