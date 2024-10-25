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

package com.teixeira.vcspace.plugins

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import bsh.Interpreter
import com.blankj.utilcode.util.ThreadUtils
import com.google.gson.GsonBuilder
import com.teixeira.vcspace.app.VCSpaceApplication
import java.io.File

class Plugin(
  val fullPath: String,
  val manifest: Manifest,
  val app: VCSpaceApplication
) {
  private lateinit var interpreter: Interpreter

  fun start(onError: (Throwable) -> Unit) {
    val helper = PluginHelper()

    try {
      interpreter = Interpreter().apply {
        setClassLoader(app.classLoader)
        eval("import java.io.*;")
        eval("import java.util.*;")
        eval("import java.util.concurrent.*;")
        eval("import java.lang.*;")
        eval("import java.net.*;")
        eval("import java.nio.*;")
        eval("import java.nio.file.*;")
        eval("import java.nio.charset.*;")
        eval("import java.nio.channels.*;")
        eval("import java.nio.charset.spi.*;")
        eval("import java.nio.file.attribute.*;")
        eval("import java.nio.file.spi.*;")
        eval("import java.security.*;")
        eval("import java.security.spec.*;")
        eval("import java.security.cert.*;")
        eval("import java.text.*;")
        eval("import java.time.*;")
        eval("import java.time.format.*;")
        eval("import java.time.temporal.*;")
        set("app", app)
        set("manifest", manifest)
        set("helper", helper)

        manifest.scripts.forEach { script ->
          source(File("$fullPath/${script.name}"))

          // Check if the "entryPoint" function exists in the current script
          val entryPoint = nameSpace.getMethod(script.entryPoint, arrayOfNulls<Class<*>>(0))

          if (entryPoint != null && entryPoint.parameterTypes.isEmpty()) {
            runCatching {
              // Invoke the "entryPoint" function
              entryPoint.invoke(arrayOfNulls<Any>(0), this)
            }.onFailure {
              ThreadUtils.runOnUiThread {
                onError(it)
              }
              it.printStackTrace()
            }
          }
        }
      }
    } catch (err: Exception) {
      onError(err)
      err.printStackTrace()

      Handler(Looper.getMainLooper()).post {
        Toast.makeText(app, err.message, Toast.LENGTH_SHORT).show()
      }
    }
  }

  fun saveManifest(manifest: Manifest) {
    val manifestFile = File("$fullPath/manifest.json")
    manifestFile.writeText(GsonBuilder().setPrettyPrinting().create().toJson(manifest))
  }
}