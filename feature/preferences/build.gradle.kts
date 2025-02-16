plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.teixeira.vcspace.preferences"

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures { viewBinding = true }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.preference)
    implementation(libs.google.material)
    implementation(project(":core:common"))
    implementation(project(":core:resources"))
}
