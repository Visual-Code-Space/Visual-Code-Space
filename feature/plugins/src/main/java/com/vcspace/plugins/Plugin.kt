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

package com.vcspace.plugins

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import bsh.Interpreter
import com.google.gson.GsonBuilder
import java.io.File

class Plugin(
  val fullPath: String,
  val manifest: Manifest,
  val app: Application
) {
  private lateinit var interpreter: Interpreter

  fun start(onError: (Exception) -> Unit) {
    val helper = PluginHelper()

    try {
      interpreter = Interpreter().apply {
        setClassLoader(app.classLoader)
        eval("import com.teixeira.vcspace.activities.editor.*;")
        eval("import com.teixeira.vcspace.activities.*;")
        eval("import com.teixeira.vcspace.providers.*;")
        eval("import com.teixeira.vcspace.utils.*;")
        eval("import com.teixeira.vcspace.app.*;")
        eval("import com.teixeira.vcspace.editor.*;")
        eval("import com.teixeira.vcspace.*;")
        eval("import android.util.*;")
        eval("import android.os.*;")
        eval("import android.content.*;")
        eval("import android.content.pm.*;")
        eval("import android.view.*;")
        eval("import android.widget.*;")
        eval("import android.app.*;")
        eval("import androidx.appcompat.app.*;")
        eval("import androidx.core.content.*;")
        eval("import androidx.core.view.*;")
        eval("import androidx.core.app.*;")
        eval("import androidx.core.graphics.*;")
        eval("import androidx.core.util.*;")
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
            // Invoke the "entryPoint" function
            helper.runOnUiThread { entryPoint.invoke(arrayOfNulls<Any>(0), this) }
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