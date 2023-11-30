plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
}

kotlin {
  kmpTargets(
    project = project,
    android = false,
    androidNative = true,
    jvm = true,
    ios = true,
    macos = true,
    tvos = true,
    watchos = true,
    linux = true,
    mingw = true,
    wasmJs = false,
    wasmWasi = false,
    js = true,
    jsBrowser = false,
  )

  sourceSets {
    commonMain {
      dependencies {
        api(projects.jsonpathCore)
        api(libs.kotlinx.serialization.json)
      }
    }
  }
}
