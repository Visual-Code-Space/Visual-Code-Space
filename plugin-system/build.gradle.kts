plugins {
  id("com.android.library")
  id("kotlin-android")
}

android {
  namespace = "com.raredev.vcspace.plugin"

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
}

dependencies {
  implementation(libs.androidx.appcompat)
  implementation(libs.common.eventbus)
  implementation(libs.common.utilcode)
  implementation(libs.common.pf4j)

  implementation(projects.common)
  implementation(projects.eventbusEvents)
}
