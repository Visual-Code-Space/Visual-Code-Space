plugins {
  id("com.android.application")
  id("kotlin-android")
  id("kotlin-parcelize")
  id("androidx.navigation.safeargs.kotlin")
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
  }
}

dependencies {
  implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

  coreLibraryDesugaring(libs.androidx.desugar)
  implementation(libs.androidx.annotation)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.core)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.fragment.ktx)
  implementation(libs.androidx.preference)
  implementation(libs.androidx.nav.fragment)
  implementation(libs.androidx.nav.ui)

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

  implementation(project(":core:common"))
  implementation(project(":core:resources"))
  implementation(project(":feature:editor"))
  implementation(project(":feature:preferences"))

  debugImplementation(libs.common.leakcanary)
}
