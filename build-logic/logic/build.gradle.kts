plugins { `kotlin-dsl` }

repositories {
  google()
  gradlePluginPortal()
  mavenCentral()
}

java {
  targetCompatibility = JavaVersion.VERSION_17
  sourceCompatibility = JavaVersion.VERSION_17
}

kotlin { jvmToolchain(17) }
