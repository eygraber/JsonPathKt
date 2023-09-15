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
}

gradleConventions {
  kotlin {
    explicitApiMode = ExplicitApiMode.Disabled
  }
}
