@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  includeBuild("build-logic")
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
    maven { url = uri("https://jitpack.io") }
  }
}

rootProject.name = "VCSpace"

include(
    ":app",
    ":common",
    ":common-res",
    ":editor-ace",
    ":editor-sora",
    ":editor-sora:lang-textmate",
    ":eventbus-events",
    ":emulatorview",
    ":models",
    ":plugin-system")
