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

package com.vcspace.plugins.internal.extensions

import android.app.Application
import android.widget.Toast
import com.google.gson.Gson
import com.teixeira.vcspace.extensions.toFile
import com.teixeira.vcspace.preferences.pluginsPath
import com.vcspace.plugins.Manifest
import com.vcspace.plugins.Plugin

fun Application.loadPlugins(): List<Plugin> {
  val plugins = mutableListOf<Plugin>()

  val pluginHome = pluginsPath.toFile()
  if (!pluginHome.exists()) pluginHome.mkdirs()

  pluginHome.listFiles()?.forEach {
    val manifestFile = it.resolve("manifest.json")
    if (!manifestFile.exists()) return@forEach

    val manifest: Manifest
    try {
      val json = manifestFile.readText()
      manifest = Gson().fromJson(json, Manifest::class.java)
    } catch (e: Exception) {
      Toast.makeText(
        this,
        "Invalid manifest.json for plugin ${it.name}",
        Toast.LENGTH_SHORT
      ).show()
      return@forEach
    }

    plugins.add(Plugin(fullPath = it.absolutePath, manifest = manifest, app = this))
  }

  return plugins
}