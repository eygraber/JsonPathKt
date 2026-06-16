import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-detekt2")
}

kotlin {
  defaultKmpTargets(
    project = project,
  )

  // androidNativeArm32 is excluded because kotest dropped support for it in 6.2.0
  androidNativeArm64()
  androidNativeX64()
  androidNativeX86()
}

gradleConventions {
  kotlin {
    explicitApiMode = ExplicitApiMode.Disabled
  }
}
