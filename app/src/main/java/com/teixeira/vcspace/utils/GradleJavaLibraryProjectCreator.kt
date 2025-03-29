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

package com.teixeira.vcspace.utils

import android.content.Context
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.ToastUtils
import com.teixeira.vcspace.extensions.extractZipFile
import com.teixeira.vcspace.extensions.toFile
import com.teixeira.vcspace.tasks.Downloader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files

const val ANDROID_JAR =
    "https://github.com/Sable/android-platforms/raw/refs/heads/master/android-35/android.jar"
val ANDROID_JAR_PATH = "${PathUtils.getInternalAppFilesPath()}/android.jar"

object GradleJavaLibraryProjectCreator {
    suspend fun createGradleJavaLibraryProject(
        context: Context,
        baseDir: String,
        packageName: String,
        fullClassName: String
    ) {
        val className = fullClassName.substringAfterLast(".")

        withContext(Dispatchers.IO) {
            val base = baseDir.toFile()
            val projectPath = base.resolve("app")
            if (!projectPath.exists()) projectPath.mkdirs()

            val libsPath = projectPath.resolve("libs")
            Files.createDirectories(libsPath.toPath())

            context.assets.open("plugin/plugins-api.jar").use {
                libsPath.resolve("plugins-api.jar").writeBytes(it.readBytes())
            }

            val srcMainJava = projectPath.resolve("src/main/java")
            val srcMainResources = projectPath.resolve("src/main/res")
            Files.createDirectories(srcMainJava.toPath())
            Files.createDirectories(srcMainResources.toPath())

            val packagePath = srcMainJava.resolve(packageName.replace(".", "/"))
            Files.createDirectories(packagePath.toPath())

            projectPath.resolve("src/main/AndroidManifest.xml").writeText("""
                <?xml version="1.0" encoding="utf-8"?>
                <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                    xmlns:tools="http://schemas.android.com/tools">

                    <application
                        android:allowBackup="true"
                        android:dataExtractionRules="@xml/data_extraction_rules"
                        android:fullBackupContent="@xml/backup_rules"
                        android:icon="@mipmap/ic_launcher"
                        android:label="@string/app_name"
                        android:roundIcon="@mipmap/ic_launcher_round"
                        android:supportsRtl="true"
                        android:theme="@style/Theme.${base.name}"
                        tools:targetApi="31" />

                </manifest>
            """.trimIndent())

            val resZip = srcMainResources.resolve("res.zip")
            context.assets.open("plugin/res.zip").use {
                resZip.writeBytes(it.readBytes())
            }
            resZip.extractZipFile(projectPath.resolve("src/main"))
            resZip.delete()

            srcMainResources.resolve("values/strings.xml").writeText("""
                <resources>
                    <string name="app_name">${base.name}</string>
                </resources>
            """.trimIndent())

            srcMainResources.resolve("values/themes.xml").writeText("""
                <resources xmlns:tools="http://schemas.android.com/tools">
                    <!-- Base application theme. -->
                    <style name="Theme.${base.name}" parent="Theme.MaterialComponents.DayNight.DarkActionBar">
                        <!-- Primary brand color. -->
                        <item name="colorPrimary">@color/purple_500</item>
                        <item name="colorPrimaryVariant">@color/purple_700</item>
                        <item name="colorOnPrimary">@color/white</item>
                        <!-- Secondary brand color. -->
                        <item name="colorSecondary">@color/teal_200</item>
                        <item name="colorSecondaryVariant">@color/teal_700</item>
                        <item name="colorOnSecondary">@color/black</item>
                        <!-- Status bar color. -->
                        <item name="android:statusBarColor">?attr/colorPrimaryVariant</item>
                        <!-- Customize your theme here. -->
                    </style>
                </resources>
            """.trimIndent())

            srcMainResources.resolve("values-night/themes.xml").writeText("""
                <resources xmlns:tools="http://schemas.android.com/tools">
                    <!-- Base application theme. -->
                    <style name="Theme.${base.name}" parent="Theme.MaterialComponents.DayNight.DarkActionBar">
                        <!-- Primary brand color. -->
                        <item name="colorPrimary">@color/purple_200</item>
                        <item name="colorPrimaryVariant">@color/purple_700</item>
                        <item name="colorOnPrimary">@color/black</item>
                        <!-- Secondary brand color. -->
                        <item name="colorSecondary">@color/teal_200</item>
                        <item name="colorSecondaryVariant">@color/teal_200</item>
                        <item name="colorOnSecondary">@color/black</item>
                        <!-- Status bar color. -->
                        <item name="android:statusBarColor">?attr/colorPrimaryVariant</item>
                        <!-- Customize your theme here. -->
                    </style>
                </resources>
            """.trimIndent())

            val settingsGradle = base.resolve("settings.gradle.kts")

            val settingsContent = """
                pluginManagement {
                    repositories {
                        google {
                            content {
                                includeGroupByRegex("com\\.android.*")
                                includeGroupByRegex("com\\.google.*")
                                includeGroupByRegex("androidx.*")
                            }
                        }
                        mavenCentral()
                        gradlePluginPortal()
                    }
                }
                dependencyResolutionManagement {
                    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
                    repositories {
                        google()
                        mavenCentral()
                    }
                }

                rootProject.name = "${base.name}"
                include(":app")
            """.trimIndent()

            settingsGradle.writeText(settingsContent)

            projectPath.resolve("build.gradle.kts").apply {
                writeText(
                    """
                        plugins {
                            alias(libs.plugins.android.application)
                        }

                        android {
                            namespace = "$packageName"
                            compileSdk = 35

                            defaultConfig {
                                applicationId = "$packageName"
                                minSdk = 26
                                targetSdk = 35
                                versionCode = 1
                                versionName = "1.0"
                            }

                            buildTypes {
                                release {
                                    isMinifyEnabled = false
                                    proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
                                }
                            }
                            compileOptions {
                                sourceCompatibility = JavaVersion.VERSION_17
                                targetCompatibility = JavaVersion.VERSION_17
                            }
                        }

                        dependencies {
                            implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

                            implementation(libs.appcompat)
                            implementation(libs.material)
                        }
                    """.trimIndent()
                )
            }

            projectPath.resolve("proguard-rules.pro").writeText("""
                # Add project specific ProGuard rules here.
                # You can control the set of applied configuration files using the
                # proguardFiles setting in build.gradle.
                #
                # For more details, see
                #   http://developer.android.com/guide/developing/tools/proguard.html

                # If your project uses WebView with JS, uncomment the following
                # and specify the fully qualified class name to the JavaScript interface
                # class:
                #-keepclassmembers class fqcn.of.javascript.interface.for.webview {
                #   public *;
                #}

                # Uncomment this to preserve the line number information for
                # debugging stack traces.
                #-keepattributes SourceFile,LineNumberTable

                # If you keep the line number information, uncomment this to
                # hide the original source file name.
                #-renamesourcefileattribute SourceFile
            """.trimIndent())

            val gradlewZip = base.resolve("gradlew.zip")
            context.assets.open("plugin/gradlew.zip").use {
                gradlewZip.writeBytes(it.readBytes())
            }

            gradlewZip.extractZipFile(base)
            gradlewZip.delete()

            val javaClassContent = """
                package $packageName;

                import androidx.annotation.NonNull;

                import com.vcspace.plugins.Plugin;
                import com.vcspace.plugins.PluginContext;

                public class $className implements Plugin {
                    @Override
                    public void onPluginLoaded(@NonNull PluginContext pluginContext) {
                        pluginContext.toast("Hello from $className");
                    }
                }

            """.trimIndent()

            packagePath.resolve("$className.java").writeText(javaClassContent)

            val buildPluginSh = base.resolve("build.sh")
            context.assets.open("plugin/build.sh").bufferedReader().use {
                buildPluginSh.writeText(it.readText())
            }
            buildPluginSh.setExecutable(true, false)
        }
    }

    private suspend fun downloadAndroidJar(
        outputFile: File,
        onDownloadComplete: (File) -> Unit
    ) {
        withContext(currentCoroutineContext()) {
            try {
                Downloader
                    .download(url = ANDROID_JAR, outputFile = outputFile)
                    .collect { progress ->
                        if (progress.error != null) {
                            throw progress.error!!
                        }
                        if (progress.isCompleted) {
                            onDownloadComplete(outputFile)
                        }
                    }
            } catch (e: Exception) {
                outputFile.delete() // Clean up incomplete file
                ToastUtils.showLong("Failed to download Android JAR")
                throw e
            }
        }
    }
}
