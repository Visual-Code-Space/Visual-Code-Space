@SuppressWarnings("unused")
object Versions {
  const val versionCode = 5
  const val version = "1.2.0-beta"

  val versionName by lazy {
    if (CI.isCiBuild) {
      "$version-${CI.commitHash}-SNAPSHOT"
    } else version
  }

  const val buildToolsVersion = "34.0.0"
  const val compileSdkVersion = 34
  const val minSdkVersion = 26
  const val targetSdkVersion = 34
}
