plugins {
  id("com.android.library")
  id("kotlin-android")
}

android {
  namespace = "com.vcspace.plugins"

  defaultConfig {
    minSdk = 26

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
  }

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

  implementation(libs.common.utilcode)

  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  implementation(libs.androidx.material.icons.extended)

  testImplementation(libs.junit)

  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
}

tasks.register<Copy>("copyJarToAssets") {
  val sourceModuleName = "feature/plugins"
  val targetModuleName = "app"
  val jarFileName = "intermediates/full_jar/release/createFullJarRelease/full.jar"
  val renamedFileName = "plugins-api.jar"

  val sourceJar = file("$rootDir/$sourceModuleName/build/$jarFileName")

  val destinationDir = file("$rootDir/$targetModuleName/src/main/assets/plugin")

  from(sourceJar)
  into(destinationDir)
  rename { renamedFileName }

  dependsOn("assembleRelease")

  doFirst {
    destinationDir.mkdirs()
  }
}
