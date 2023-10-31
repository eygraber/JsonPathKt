import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-detekt")
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
  )
}

gradleConventions {
  kotlin {
    explicitApiMode = ExplicitApiMode.Disabled
  }
}
