import java.util.Properties

/*
 * This file is part of Visual Code Space.
 *
 * Visual Code Space is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Visual Code Space is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Visual Code Space.
 * If not, see <https://www.gnu.org/licenses/>.
 */

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin)
}

android {
  namespace = "com.vcspace.plugins"

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  defaultConfig {
    val file = project.rootProject.file("token.properties")
    
    val githubToken = if (file.exists()) {
      val properties = Properties().also { it.load(file.inputStream()) }
      properties.getProperty("VCSPACE_TOKEN") ?: ""
    } else ""

    buildConfigField("String", "GITHUB_TOKEN", "\"$githubToken\"")
  }

  buildFeatures {
    buildConfig = true
  }
}

dependencies {
  implementation(libs.androidx.appcompat)
  implementation(libs.google.material)
  implementation(libs.google.gson)
  implementation(libs.bsh)
  implementation(libs.common.utilcode)

  implementation(libs.retrofit)
  implementation(libs.retrofit.converter.gson)
  implementation(libs.okhttp)

  implementation(project(":core:common"))
  implementation(project(":core:resources"))
  implementation(project(":feature:preferences"))
}
