plugins {
  id("com.android.library")
  id("kotlin-android")
}

android {
  namespace = "com.teixeira.vcspace.resources"

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  buildFeatures { viewBinding = true }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
}

dependencies {
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.preference)
  implementation(libs.google.material)
}
