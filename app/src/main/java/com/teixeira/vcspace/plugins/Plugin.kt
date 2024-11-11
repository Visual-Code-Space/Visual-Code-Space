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

import android.widget.Toast
import bsh.Interpreter
import com.blankj.utilcode.util.ThreadUtils
import com.google.gson.GsonBuilder
import com.teixeira.vcspace.app.VCSpaceApplication
import com.teixeira.vcspace.editor.snippet.SnippetController
import com.teixeira.vcspace.plugins.helper.CommandManager
import com.teixeira.vcspace.plugins.helper.EditorHelper
import com.teixeira.vcspace.plugins.helper.PluginHelper
import com.teixeira.vcspace.preferences.pluginsPath
import java.io.File

class Plugin(
  val fullPath: String,
  val manifest: Manifest,
  val app: VCSpaceApplication
) {
  private lateinit var interpreter: Interpreter

  fun start(onError: (Throwable) -> Unit) {
    val helper = PluginHelper()
    val commandManager = CommandManager()
    val snippetController = SnippetController.instance
    val editorHelper = EditorHelper(app.getEditorActivity())

    try {
      interpreter = Interpreter().apply {
        setClassLoader(app.classLoader)
        strictJava = true
        nameSpace.importClass("com.teixeira.vcspace.plugins.helper.FileHelper")
        nameSpace.importClass("com.teixeira.vcspace.plugins.extension.Extension")
        nameSpace.loadDefaultImports()

        set("app", app)
        set("manifest", manifest)
        set("helper", helper)
        set("commandManager", commandManager)
        set("editorHelper", editorHelper)
        set("snippetController", snippetController)
        set("PLUGIN_DIR", pluginsPath)
        set("CURRENT_PLUGIN_DIR", fullPath)

        eval(
          """
          public static void showMessage(String message) {
            Toast.makeText(app, message, Toast.LENGTH_SHORT).show();
          }
        """.trimIndent()
        )

        manifest.scripts.forEach { script ->
          source(File("$fullPath/${script.name}"))

          // Check if the "entryPoint" function exists in the current script
          val entryPoint = nameSpace.getMethod(script.entryPoint, arrayOfNulls<Class<*>>(0))

          if (entryPoint != null && entryPoint.parameterTypes.isEmpty()) {
            runCatching {
              ThreadUtils.runOnUiThread {
                // Invoke the "entryPoint" function
                entryPoint.invoke(arrayOfNulls<Any>(0), this)
              }
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
    }
  }

  private fun showToast(message: String) {
    ThreadUtils.runOnUiThread { Toast.makeText(app, message, Toast.LENGTH_SHORT).show() }
  }

  fun saveManifest(manifest: Manifest) {
    val manifestFile = File("$fullPath/manifest.json")
    manifestFile.writeText(GsonBuilder().setPrettyPrinting().create().toJson(manifest))
  }
}