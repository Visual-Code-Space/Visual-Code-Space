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

package com.teixeira.vcspace.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vcspace.plugins.Plugin
import com.vcspace.plugins.internal.PluginManager
import com.vcspace.plugins.internal.distribution.github.Content
import kotlinx.coroutines.launch

class PluginViewModel : ViewModel() {
  private val _installedPlugins = mutableStateOf(mutableListOf<Plugin>())
  private val _plugins = mutableStateOf(mutableListOf<Content>())
  private val _isLoadingInstalledPlugins = mutableStateOf(true)
  private val _isLoadingPlugins = mutableStateOf(true)

  val installedPlugins get() = _installedPlugins
  val plugins get() = _plugins
  val isLoadingInstalledPlugins get() = _isLoadingInstalledPlugins
  val isLoadingPlugins get() = _isLoadingPlugins

  fun loadInstalledPlugins() {
    _isLoadingInstalledPlugins.value = true

    viewModelScope.launch {
      _installedPlugins.value = PluginManager.getPlugins().toMutableList()
    }.invokeOnCompletion {
      _isLoadingInstalledPlugins.value = false
    }
  }

  fun addNewPlugin(plugin: Plugin) {
    val newList = _installedPlugins.value
    newList.add(plugin)
    _installedPlugins.value = newList
  }

  fun loadPlugins() {
    _isLoadingPlugins.value = true
    val list = mutableListOf<Content>()

    viewModelScope.launch {
      PluginManager.fetchPluginsFromGithub { contents ->
        val dirs = contents?.filter { it.type == "dir" } ?: emptyList()
        dirs.forEach { content ->
          PluginManager.getPluginSize(content.path) { size ->
            list.add(content.copy(size = size.toInt()))
            if (list.size == dirs.size) {
              _plugins.value = list
              _isLoadingPlugins.value = false
            }
          }
        }
        if (dirs.isEmpty()) _isLoadingPlugins.value = false
      }
    }
  }
}