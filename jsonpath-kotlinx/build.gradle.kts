import org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeTargetPreset
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
}

kotlin {
  targets {
    kmpTargets(
      project = project,
      android = false,
      jvm = true,
      ios = true,
      macos = true,
      wasm = false,
      js = true,
    )

    presets.withType<AbstractKotlinNativeTargetPreset<*>>().forEach {
      if (it.konanTarget !in KonanTarget.deprecatedTargets) {
        targetFromPreset(it)
      }
    }
  }

  sourceSets {
    commonMain {
      dependencies {
        api(projects.jsonpathCore)
        api(libs.kotlinx.serialization.json)
      }
    }

    getByName("commonTest") {
      dependencies {
        implementation(projects.jsonpathTest)

        implementation(libs.test.kotest.assertions)
        implementation(kotlin("test"))
      }
    }
  }
}
