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

package com.teixeira.vcspace.ui.screens.plugin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teixeira.vcspace.github.Content
import com.teixeira.vcspace.plugins.Plugin
import com.teixeira.vcspace.plugins.internal.PluginManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InstalledPluginState(
  val plugins: List<Plugin> = emptyList(),
  val isLoading: Boolean = false
)

data class PluginState(
  val plugins: List<Content> = emptyList(),
  val isLoading: Boolean = false
)

class PluginViewModel : ViewModel() {
  private val _installedPluginState = MutableStateFlow(InstalledPluginState())
  private val _pluginState = MutableStateFlow(PluginState())

  val installedPluginState = _installedPluginState.asStateFlow()
  val pluginState = _pluginState.asStateFlow()

  fun loadInstalledPlugins() {
    _installedPluginState.update { it.copy(isLoading = true) }

    viewModelScope.launch {
      // Fetch installed plugins asynchronously
      val installedPlugins = PluginManager.getPlugins().toList()
      _installedPluginState.update {
        it.copy(plugins = installedPlugins, isLoading = false)
      }
    }
  }

  fun addNewInstalledPlugin(plugin: Plugin) {
    _installedPluginState.update {
      val updatedList = it.plugins.toMutableList().apply { add(plugin) }
      it.copy(plugins = updatedList)
    }
  }

  // Load Available Plugins
  fun loadPlugins() {
    _pluginState.update { it.copy(isLoading = true) }

    viewModelScope.launch {
      val pluginList = mutableListOf<Content>()

      PluginManager.fetchPluginsFromGithub(
        onSuccess = { contents ->
          val dirs = contents.filter { it.type == "dir" }

          if (dirs.isEmpty()) {
            _pluginState.update { it.copy(isLoading = false) }
          } else {
            dirs.forEach { content ->
              viewModelScope.launch {
                PluginManager.getPluginSize(
                  path = content.path,
                  onSuccess = { size ->
                    pluginList.add(content.copy(size = size))

                    // When all plugins are loaded, update the state
                    if (pluginList.size == dirs.size) {
                      _pluginState.update {
                        it.copy(plugins = pluginList, isLoading = false)
                      }
                    }
                  },
                  onFailure = {
                    it.printStackTrace()
                  }
                )
              }
            }
          }
        },
        onFailure = {
          it.printStackTrace()
        }
      )
    }
  }

  fun downloadPlugin(
    plugin: Content,
    onSuccess: (Plugin) -> Unit,
    onFailure: (Throwable) -> Unit
  ) {
    viewModelScope.launch {
      _installedPluginState.update { it.copy(isLoading = true) }
      _pluginState.update { it.copy(isLoading = true) }

      PluginManager.downloadPlugin(
        plugin = plugin,
        onSuccess = { result ->
          loadInstalledPlugins()
          _pluginState.update { it.copy(isLoading = false) }
          onSuccess(result)
        },
        onFailure = { error ->
          loadInstalledPlugins()
          _pluginState.update { it.copy(isLoading = false) }
          onFailure(error)
        }
      )
    }
  }
}
