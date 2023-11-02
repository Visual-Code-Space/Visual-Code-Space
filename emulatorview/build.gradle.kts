plugins {
  id("com.android.library")
  id("kotlin-android")
}

android {
  namespace = "com.raredev.terminal.view"

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
}

dependencies {
  implementation(libs.androidx.annotation)
  implementation(libs.androidx.appcompat)
}