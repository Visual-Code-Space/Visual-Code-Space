@file:Suppress("UnstableApiUsage")

pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    maven("https://cache-redirector.jetbrains.com/kotlin.bintray.com/kotlin-plugin")
  }
}

//plugins {
//  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
//}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    maven { url = uri("https://jitpack.io") }
  }
}

rootProject.name = "VCSpace"

include(":app", ":core:common", ":core:resources", ":feature:editor", ":feature:preferences")