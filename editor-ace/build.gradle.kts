plugins {
  id("com.android.library")
  id("kotlin-android")
}

android {
  namespace = "com.raredev.vcspace.editor.ace"

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
}

dependencies {
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.webkit)
  implementation(libs.androidx.javascriptengine)
  implementation(libs.common.eventbus)
  implementation(libs.common.utilcode)

  implementation(projects.common)
  implementation(projects.eventbusEvents)
}