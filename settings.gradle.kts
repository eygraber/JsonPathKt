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
  id("com.eygraber.conventions.settings") version "0.0.70"
  id("com.gradle.enterprise") version "3.17"
}

rootProject.name = "jsonpathkt"

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    if (System.getenv("CI") != null) {
      termsOfServiceAgree = "yes"
      publishAlways()
    }
  }
}

include(":jsonpath-benchmarks")
include(":jsonpath-core")
include(":jsonpath-jsonjava")
include(":jsonpath-kotlinx")
include(":jsonpath-test")
