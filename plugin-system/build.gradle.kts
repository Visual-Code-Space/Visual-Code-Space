plugins {
  id("com.android.library")
  id("kotlin-android")
}

android {
  namespace = "com.raredev.vcspace.pluginsystem"
  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.google.material)
  implementation(libs.common.pf4j)
  implementation(libs.common.eventbus)

  implementation(projects.common)
  implementation(projects.eventbusEvents)

  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
}