plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
  alias(libs.plugins.kotlinx.serialization)
}

kotlin {
  defaultKmpTargets(
    project = project,
  )

  sourceSets {
    getByName("commonTest") {
      dependencies {
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
}
