plugins {
  id("com.android.application")
  id("kotlin-android")
  id("com.google.android.gms.oss-licenses-plugin")
  id("androidx.navigation.safeargs.kotlin")
}

android {
  namespace = "com.raredev.vcspace"

  defaultConfig {
    applicationId = "com.raredev.vcspace"

    vectorDrawables.useSupportLibrary = true
  }

  signingConfigs {
    create("general") {
      storeFile = file("vcspace.keystore")
      keyAlias = "vcspace"
      keyPassword = "raredevKey"
      storePassword = "raredevKey"
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
        arrayOf("META-INF/README.md", "META-INF/CHANGES", "bundle.properties", "plugin.properties"))
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
  implementation(libs.google.oss.licenses)
  implementation(libs.google.guava)
  implementation(libs.google.gson)

  implementation(libs.common.editor)
  implementation(libs.common.utilcode)
  implementation(libs.common.jgit)
  implementation(libs.common.eventbus)
  implementation(libs.common.android.coroutines)

  implementation(projects.common)
  implementation(projects.commonRes)
  implementation(projects.editorAce)
  implementation(projects.editorSora)
  implementation(projects.editorSora.langTextmate)
  implementation(projects.eventbusEvents)
  implementation(projects.emulatorview)
  implementation(projects.models)
  implementation(projects.pluginSystem)

  debugImplementation(libs.common.leakcanary)
}
