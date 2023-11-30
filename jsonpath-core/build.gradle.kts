plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
  alias(libs.plugins.kotlinx.serialization)
}

kotlin {
  kmpTargets(
    project = project,
    android = false,
    androidNative = true,
    jvm = true,
    ios = true,
    tvos = true,
    watchos = true,
    macos = true,
    linux = true,
    mingw = true,
    wasmJs = false,
    wasmWasi = false,
    js = true,
    jsBrowser = false,
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
