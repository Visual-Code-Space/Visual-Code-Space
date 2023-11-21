plugins {
  id("com.android.library")
  id("kotlin-android")
}

android {
  namespace = "com.raredev.vcspace.events"

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
}

dependencies { implementation(libs.common.eventbus) }
