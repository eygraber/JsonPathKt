import com.eygraber.conventions.tasks.deleteRootBuildDirWhenCleaning
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

buildscript {
  dependencies {
    classpath(libs.buildscript.detekt)
    classpath(libs.buildscript.dokka)
    classpath(libs.buildscript.kotlin)
    classpath(libs.buildscript.publish)
  }
}

plugins {
  base
  alias(libs.plugins.conventions)
}

deleteRootBuildDirWhenCleaning()

gradleConventionsDefaults {
  kotlin {
    jvmTargetVersion = JvmTarget.JVM_1_8
    allWarningsAsErrors = true
    explicitApiMode = ExplicitApiMode.Strict
  }
}

gradleConventionsKmpDefaults {
  targets(
    KmpTarget.AndroidNative,
    KmpTarget.Ios,
    KmpTarget.Js,
    KmpTarget.Jvm,
    KmpTarget.Linux,
    KmpTarget.Macos,
    KmpTarget.Mingw,
    KmpTarget.Tvos,
    KmpTarget.Watchos,
  )
}
