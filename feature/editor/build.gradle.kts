plugins {
  id("com.android.library")
  id("kotlin-android")
  alias(libs.plugins.kotlin.compose)
}

android {
  namespace = "com.teixeira.vcspace.editor"

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  buildFeatures {
    viewBinding = true
    compose = true
  }
}

dependencies {
  implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

  implementation(libs.androidx.appcompat)
  implementation(libs.google.material)
  implementation(libs.google.guava)
  implementation(libs.google.gson)
  implementation(libs.common.editor)
  implementation(libs.common.editor.lsp)
  implementation(libs.common.editor.textmate)
  implementation(libs.common.eventbus)
  implementation(libs.common.utilcode)
  implementation(libs.common.jsoup)

  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.viewbinding)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  implementation(libs.androidx.material.icons.extended)

  implementation(libs.org.eclipse.lsp4j)
  implementation(libs.org.eclipse.lsp4j.jsonrpc)

  implementation(project(":core:common"))
  implementation(project(":core:resources"))
}
