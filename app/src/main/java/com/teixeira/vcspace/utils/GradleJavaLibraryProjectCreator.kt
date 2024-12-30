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
import com.teixeira.vcspace.extensions.extractZipFile
import com.teixeira.vcspace.extensions.toFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Files

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
      val projectPath = base.resolve("plugin")
      if (!projectPath.exists()) projectPath.mkdirs()

      val libsPath = projectPath.resolve("libs")
      Files.createDirectories(libsPath.toPath())

      context.assets.open("plugin/android.jar").use {
        libsPath.resolve("android.jar").writeBytes(it.readBytes())
      }

      context.assets.open("plugin/plugins-api.jar").use {
        libsPath.resolve("plugins-api.jar").writeBytes(it.readBytes())
      }

      val srcMainJava = projectPath.resolve("src/main/java")
      val srcMainResources = projectPath.resolve("src/main/resources")
      Files.createDirectories(srcMainJava.toPath())
      Files.createDirectories(srcMainResources.toPath())

      val packagePath = srcMainJava.resolve(packageName.replace(".", "/"))
      Files.createDirectories(packagePath.toPath())

      val settingsGradle = base.resolve("settings.gradle.kts")

      val settingsContent = """
        @file:Suppress("UnstableApiUsage")

        plugins {
            // Apply the foojay-resolver plugin to allow automatic download of JDKs
            id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
        }

        dependencyResolutionManagement {
            repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
            repositories {
                google()
                mavenCentral()
            }
        }

        rootProject.name = "${base.name}"
        include("plugin")
      """.trimIndent()

      val buildContentExtra = """
        tasks.register<Jar>("fatJar") {
            group = "build"
            description = "Assembles a fat JAR file containing all dependencies."

            archiveBaseName.set("${base.name.lowercase().replace(" ", "-")}-all")

            from({
                configurations.runtimeClasspath.get().filter {
                    it.name.endsWith(".jar") && it.name.equals("android.jar").not()
                }.map { zipTree(it) }
            })

            from(sourceSets.main.get().output)

            exclude("com/google/**")

            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }

        tasks.build {
            dependsOn(tasks["fatJar"])
        }
      """.trimIndent()

      settingsGradle.writeText(settingsContent)
      context.assets.open("plugin/build.gradle.kts").bufferedReader().use {
        projectPath.resolve("build.gradle.kts").apply {
          writeText(it.readText())
          appendText("\n$buildContentExtra")
        }
      }

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
            public void onPluginLoaded(@NonNull PluginContext context) {
                context.toast("Hello from $className!");
            }
        }
        
      """.trimIndent()

      packagePath.resolve("$className.java").writeText(javaClassContent)

      val buildPluginSh = base.resolve("build_plugin.sh")
      val buildPluginContent = """
        #!/bin/bash

        set -e

        OUTPUT_DIR="plugin/build/libs"
        INPUT_JAR="${"$"}OUTPUT_DIR/${base.name.lowercase().replace(" ", "-")}-all.jar"
        DEX_OUTPUT_DIR="plugin/build/dex"
        DEX_FILE="${"$"}DEX_OUTPUT_DIR/${base.name.lowercase().replace(" ", "-")}-all.jar"
        PLUGIN_PROPERTIES="plugin.properties"
        ZIP_FILE="${base.name}.zip"
        
        command_exists() {
            command -v "${'$'}1" >/dev/null 2>&1
        }

        # Ensure gradle is installed
        if ! command_exists gradle; then
            echo "Gradle not found. Installing gradle..."
            apt update
            apt install -y gradle
        else
            echo "Gradle is installed."
        fi

        # Ensure d8 is installed
        if ! command_exists d8; then
            echo "d8 not found. Installing d8..."
            apt update
            apt install -y d8
        else
            echo "d8 is installed."
        fi

        echo "Running Gradle build..."
        gradle build

        if [ ! -f "${"$"}INPUT_JAR" ]; then
          echo "Error: JAR file not found at ${"$"}INPUT_JAR"
          exit 1
        fi

        echo "Running d8 to convert JAR to DEX format..."
        mkdir -p "${"$"}DEX_OUTPUT_DIR"
        d8 "${"$"}INPUT_JAR" --output "${"$"}DEX_FILE"

        if [ ! -f "${"$"}DEX_FILE" ]; then
          echo "Error: DEX file not created."
          exit 1
        fi

        if [ ! -f "${"$"}PLUGIN_PROPERTIES" ]; then
          echo "Error: plugin.properties file not found."
          exit 1
        fi

        echo "Creating ZIP file..."
        zip -j "${"$"}ZIP_FILE" "${"$"}DEX_FILE" "${"$"}PLUGIN_PROPERTIES"

        echo "ZIP file created successfully: ${"$"}ZIP_FILE"
      """.trimIndent()

      buildPluginSh.writeText(buildPluginContent)
      buildPluginSh.setExecutable(true, false)
    }
  }
}
