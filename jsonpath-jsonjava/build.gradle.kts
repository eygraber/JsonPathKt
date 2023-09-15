plugins {
  kotlin("jvm")
  id("com.eygraber.conventions-kotlin-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
}

dependencies {
  api(projects.jsonpathCore)
  api(libs.jsonJava)
}
