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

import android.content.Context
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.teixeira.vcspace.PluginConstants
import com.teixeira.vcspace.extensions.extractZipFile
import com.teixeira.vcspace.extensions.toFile
import com.teixeira.vcspace.plugins.internal.PluginInfo
import com.teixeira.vcspace.utils.runOnUiThread
import com.teixeira.vcspace.utils.showShortToast
import com.vcspace.plugins.Plugin
import dalvik.system.DexClassLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PluginLoader {
  fun loadPlugins(context: Context): List<Pair<PluginInfo, Plugin>> {
    val plugins = mutableListOf<Plugin>()
    val pluginInfos = mutableListOf<PluginInfo>()

    val pluginsPath = PluginConstants.PLUGIN_HOME_PATH.toFile()
    FileUtils.createOrExistsDir(pluginsPath)

    pluginsPath.listFiles()?.forEach { file ->
      val properties = file.resolve("plugin.properties")
      if (!properties.exists()) {
        throw IllegalArgumentException("Plugin file does not contain plugin.properties")
      }

      val pluginInfo = PluginInfo(properties)
      pluginInfo.pluginFileName?.let {
        val jarFilePath = file.resolve(it).apply {
          setWritable(false)
          setReadable(true, true)
        }

        val dexClassLoader = DexClassLoader(
          jarFilePath.absolutePath,
          null,
          null,
          context.applicationContext.classLoader
        )

        val pluginClass = dexClassLoader.loadClass(pluginInfo.mainClass)

        if (Plugin::class.java.isAssignableFrom(pluginClass)) {
          val constructor = pluginClass.getConstructor()
          plugins.add(constructor.newInstance() as Plugin)
          pluginInfos.add(pluginInfo)
        } else {
          throw IllegalArgumentException("Class does not implement Plugin interface")
        }
      } ?: runOnUiThread {
        showShortToast(context, "Plugin file not found for ${file.name}")
      }
    }

    return pluginInfos.zip(plugins)
  }

  suspend fun extractPluginZip(pluginZipFile: java.io.File) {
    withContext(Dispatchers.IO) {
      runCatching {
        val path = "${PluginConstants.PLUGIN_HOME_PATH}/${pluginZipFile.nameWithoutExtension}"
        val internalFile = path.toFile()

        FileUtils.createOrExistsDir(internalFile)
        pluginZipFile.extractZipFile(internalFile)
      }.onFailure {
        ToastUtils.showShort(it.message)
      }
    }
  }
}
