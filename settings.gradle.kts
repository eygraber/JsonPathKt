import com.eygraber.conventions.Env

pluginManagement {
  repositories {
    mavenCentral()

    gradlePluginPortal()
  }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
  // comment this out for now because it doesn't work with KMP js
  // https://youtrack.jetbrains.com/issue/KT-55620/KJS-Gradle-plugin-doesnt-support-repositoriesMode
  // repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

  @Suppress("UnstableApiUsage")
  repositories {
    mavenCentral()
  }
}

plugins {
  id("com.eygraber.conventions.settings") version "0.0.76"
  id("com.gradle.develocity") version "3.17.6"
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
