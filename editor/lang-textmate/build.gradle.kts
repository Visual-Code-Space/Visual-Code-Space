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

  buildFeatures { buildConfig = true }
}

dependencies {
  implementation(libs.androidx.annotation)
  implementation(libs.google.guava)
  implementation(libs.google.gson)
  implementation(libs.common.editor)
  implementation(libs.common.eventbus)

  implementation("org.eclipse.jdt:org.eclipse.jdt.annotation:2.2.700")
  implementation("org.jruby.jcodings:jcodings:1.0.58")
  implementation("org.jruby.joni:joni:2.2.1")
  implementation("org.yaml:snakeyaml:2.2")

  implementation(projects.common)
  implementation(projects.eventbusEvents)
}
