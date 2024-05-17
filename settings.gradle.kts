import com.eygraber.conventions.Env

pluginManagement {
  repositories {
    mavenCentral()

    gradlePluginPortal()
  }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
  @Suppress("UnstableApiUsage")
  repositories {
    mavenCentral()
  }
}

plugins {
  id("com.eygraber.conventions.settings") version "0.0.71"
  id("com.gradle.develocity") version "3.17.4"
}

rootProject.name = "jsonpathkt"

develocity {
  buildScan {
    termsOfUseUrl = "https://gradle.com/terms-of-service"
    publishing.onlyIf { Env.isCI }
    if (Env.isCI) {
      termsOfUseAgree = "yes"
    }
  }
}

include(":jsonpath-benchmarks")
include(":jsonpath-core")
include(":jsonpath-jsonjava")
include(":jsonpath-kotlinx")
include(":jsonpath-test")
