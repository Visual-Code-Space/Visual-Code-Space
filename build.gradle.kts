import com.android.build.gradle.BaseExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.model.KotlinAndroidExtension

plugins {
  id("build-logic.root-project")
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.kotlin) apply false
}

buildscript {
  dependencies {
    classpath("com.google.android.gms:oss-licenses-plugin:0.10.6")
    classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.5")
  }
}

fun Project.configureBaseExtension() {
  extensions.findByType(BaseExtension::class)?.run {
    compileSdkVersion(Versions.compileSdkVersion)
    buildToolsVersion = Versions.buildToolsVersion

    defaultConfig {
      minSdk = Versions.minSdkVersion
      targetSdk = Versions.targetSdkVersion
      versionCode = Versions.versionCode
      versionName = Versions.versionName
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

tasks.register<Delete>("clean") {
  delete(rootProject.buildDir)
}