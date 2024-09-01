plugins {
  id("com.android.application")
  id("kotlin-android")
  id("kotlin-parcelize")
  id("androidx.navigation.safeargs.kotlin")
  alias(libs.plugins.kotlin.compose)
}

android {
  namespace = "com.teixeira.vcspace"

  defaultConfig {
    applicationId = "com.teixeira.vcspace"

    vectorDrawables.useSupportLibrary = true
  }

  signingConfigs {
    create("general") {
      storeFile = file("test.keystore")
      keyAlias = "test"
      keyPassword = "teixeira0x"
      storePassword = "teixeira0x"
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      signingConfig = signingConfigs.getByName("general")
      proguardFiles("proguard-rules.pro")
    }
    debug {
      isMinifyEnabled = false
      signingConfig = signingConfigs.getByName("general")
      proguardFiles("proguard-rules.pro")
    }
  }

  compileOptions { isCoreLibraryDesugaringEnabled = true }

  packaging {
    resources.excludes.addAll(
      arrayOf("META-INF/README.md", "META-INF/CHANGES", "bundle.properties", "plugin.properties")
    )

    jniLibs { useLegacyPackaging = true }
  }

  lint { abortOnError = false }

  buildFeatures {
    viewBinding = true
    buildConfig = true
    compose = true
  }
}

dependencies {
  implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)

  coreLibraryDesugaring(libs.androidx.desugar)
  implementation(libs.androidx.annotation)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.core)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.fragment.ktx)
  implementation(libs.androidx.preference)

  implementation(libs.google.material)
  implementation(libs.google.guava)
  implementation(libs.google.gson)

  implementation(libs.common.editor)
  implementation(libs.common.editor.textmate)
  implementation(libs.common.utilcode)
  implementation(libs.common.eventbus)
  implementation(libs.common.p7zip)
  implementation(libs.common.kotlinx.coroutines.android)
  implementation(libs.common.terminal.view)
  implementation(libs.common.terminal.emulator)

  implementation(libs.androidx.nav.fragment)
  implementation(libs.androidx.nav.ui)
  implementation(libs.androidx.nav.dynamic.features)

  implementation(libs.prdownloader)

  implementation(project(":core:common"))
  implementation(project(":core:resources"))
  implementation(project(":feature:editor"))
  implementation(project(":feature:preferences"))
  implementation(project(":feature:plugins"))

  debugImplementation(libs.common.leakcanary)
  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)
}
