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
import bsh.Interpreter
import com.teixeira.vcspace.PluginConstants
import java.io.File

class Plugin(
  val fullPath: String,
  val manifest: Manifest,
  val app: Application
) : Thread() {
  private lateinit var interpreter: Interpreter

  override fun run() {
    val helper = PluginHelper()

    interpreter = Interpreter().apply {
      setClassLoader(app.classLoader)
      set("app", app)
      set("manifest", manifest)
      set("helper", helper)
      source(File("$fullPath/${manifest.path}"))
    }
  }
}