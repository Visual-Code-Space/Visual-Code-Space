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

package com.teixeira.vcspace.activities

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.teixeira.vcspace.activities.base.BaseComposeActivity
import com.teixeira.vcspace.extensions.tmpDir
import com.teixeira.vcspace.terminal.Executor
import com.teixeira.vcspace.terminal.alpineDir
import com.teixeira.vcspace.terminal.lib
import java.io.File

class TestActivity : BaseComposeActivity() {
  @SuppressLint("SetJavaScriptEnabled")
  @Composable
  override fun MainScreen() {
    LaunchedEffect(Unit) {
      val pkg = File(filesDir, "proot")
      assets.open("terminal/proot").use {
        pkg.writeBytes(it.readBytes())
      }
      pkg.setExecutable(true)

      println("Executable: ${pkg.canExecute()}")

      runCatching {
        val result = Executor.runBinary(
          pkg.absolutePath,
          args = arrayOf(
            "-r", alpineDir.absolutePath,
            "-0", "-w", "/",
            "-b", "/dev",
            "-b", "/proc",
            "-b", "/sys",
            "/bin/sh"
          ),
          env = arrayOf("LD_LIBRARY_PATH=${lib.absolutePath}")
        )

        if (result == 0) {
          println("Binary executed successfully.");
        } else {
          println("Binary execution failed with error code: $result");
        }

        // using ProcessBuilder
        val args = listOf(
          "--kill-on-exit",
          "--link2symlink",
          "-0",
          "-r",
          alpineDir.absolutePath,
          "-b",
          "/dev/",
          "-b",
          "/sys/",
          "-b",
          "/proc/",
          "-w",
          "/",
          "/usr/bin/env",
          "HOME=/home",
          "PATH=/bin:/usr/bin:/sbin:/usr/sbin"
        )
        val output = runProot(args, env = listOf("PROOT_TMP_DIR=${tmpDir.absolutePath}"))
        println("Proot output: $output")
      }.onFailure {
        println("Exception: $it")
      }
    }

    Box(
      modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding()
    ) {

    }
  }

  private fun runProot(args: List<String>, env: List<String> = emptyList()): String {
    val binaryPath = File(filesDir, "proot").absolutePath

    // Command to run
    val command = mutableListOf(binaryPath) + args

    return try {
      val builder = ProcessBuilder(command).redirectErrorStream(true) // Redirect errors to output
      val environment = builder.environment()
      env.forEach {
        val (key, value) = it.split("=")
        environment[key] = value
      }
      val process = builder.start()

      val output = process.inputStream.bufferedReader().readText()
      process.waitFor()
      output
    } catch (e: Exception) {
      "Error running proot: ${e.message}"
    }
  }
}

