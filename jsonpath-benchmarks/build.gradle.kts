import com.eygraber.conventions.kotlin.kmp.jvmMain
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeTargetPreset
import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-detekt")
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
      isJsLeafModule = true,
    )

    js(IR) {
      nodejs {
        binaries.executable()
      }
      browser {
        binaries.executable()
      }
    }

    jvm {
      @OptIn(ExperimentalKotlinGradlePluginApi::class)
      this.mainRun {
        mainClass = "com.nfeld.jsonpathkt.JvmBenchmarkKt"
      }
    }

    presets.withType<AbstractKotlinNativeTargetPreset<*>>().forEach {
      if (it.konanTarget.family != Family.ANDROID && it.konanTarget !in KonanTarget.deprecatedTargets) {
        targetFromPreset(it).binaries {
          executable()
        }
      }
    }
  }

  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.jsonpathKotlinx)
        implementation(projects.jsonpathTest)
      }
    }

    jvmMain {
      dependencies {
        implementation(projects.jsonpathJsonjava)
        implementation(libs.jackson.core)
        implementation(libs.jackson.databind)
        implementation(libs.jackson.moduleKotlin)
        implementation(libs.jayway.jsonPath)
        implementation(libs.slf4j)
      }
    }
  }
}

gradleConventions {
  kotlin {
    explicitApiMode = ExplicitApiMode.Disabled
  }
}
