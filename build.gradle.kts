import com.android.build.gradle.BaseExtension

plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.kotlin) apply false
}

buildscript {
  dependencies { classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7") }
}

fun Project.configureBaseExtension() {
  extensions.findByType(BaseExtension::class)?.run {
    compileSdkVersion(34)
    buildToolsVersion = "34.0.0"

    defaultConfig {
      minSdk = 26
      targetSdk = 34
      versionCode = 6
      versionName = "1.2.0-beta"
    }

    compileOptions {
      sourceCompatibility = JavaVersion.VERSION_17
      targetCompatibility = JavaVersion.VERSION_17
    }
  }
}

subprojects {
  plugins.withId("com.android.application") { configureBaseExtension() }
  plugins.withId("com.android.library") { configureBaseExtension() }
}

tasks.register<Delete>("clean") { delete(rootProject.buildDir) }
