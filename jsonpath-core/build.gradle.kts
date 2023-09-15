import org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeTargetPreset
import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
  alias(libs.plugins.kotlinx.serialization)
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
      if (!it.konanTarget.family.isAppleFamily && it.konanTarget !in KonanTarget.deprecatedTargets) {
        // don't add android native targets
        if (it.konanTarget.family != Family.ANDROID) {
          targetFromPreset(it)
        }
      }
    }
  }

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
