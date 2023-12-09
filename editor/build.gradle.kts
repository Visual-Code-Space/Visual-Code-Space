plugins {
  id("com.android.library")
  id("kotlin-android")
}

android {
  namespace = "com.raredev.vcspace.editor"

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  buildFeatures { viewBinding = true }
}

dependencies {
  implementation(libs.androidx.appcompat)
  implementation(libs.google.material)
  implementation(libs.google.guava)
  implementation(libs.google.gson)
  implementation(libs.common.editor)
  implementation(libs.common.eventbus)
  implementation(libs.common.utilcode)
  implementation(libs.common.jsoup)

  implementation(projects.common)
  implementation(projects.commonRes)
  implementation(projects.editorTextmate)
  implementation(projects.subprojects.tm4e)
  implementation(projects.eventbusEvents)
  implementation(projects.models)
}
