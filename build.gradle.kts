import com.eygraber.conventions.tasks.deleteRootBuildDirWhenCleaning
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin

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
    KmpTarget.WasmJs,
    KmpTarget.Watchos,
  )
}

// Workaround: mocha is not hoisted to root node_modules by npm due to transitive
// dependency version conflicts with other root-level packages (e.g. webpack).
// kotlin-web-helpers needs mocha available at the root level for its mocha reporter.
plugins.withType<NodeJsRootPlugin> {
  the<NodeJsRootExtension>().apply {
    tasks.named("rootPackageJson") {
      doLast {
        val packageJsonFile = layout.buildDirectory.file("js/package.json").get().asFile
        if (packageJsonFile.exists()) {
          val mochaVersion = versions.mocha.version
          val content = packageJsonFile.readText()
          val updated = if (""""devDependencies": {}""" in content) {
            content.replace(
              """"devDependencies": {}""",
              """"devDependencies": {
    "mocha": "$mochaVersion"
  }""",
            )
          } else if (""""mocha":""" !in content) {
            content.replace(
              """"devDependencies": {""",
              """"devDependencies": {
    "mocha": "$mochaVersion",""",
            )
          } else {
            content
          }
          packageJsonFile.writeText(updated)
        }
      }
    }
  }
}
