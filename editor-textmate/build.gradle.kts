plugins {
  id("com.android.library")
  id("kotlin-android")
}

android {
  namespace = "io.github.rosemoe.sora.langs.textmate"

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
}

dependencies {
  implementation(libs.androidx.annotation)
  implementation(libs.google.gson)
  implementation(libs.common.editor)

  implementation(projects.common)
  implementation(projects.subprojects.tm4e)
}
