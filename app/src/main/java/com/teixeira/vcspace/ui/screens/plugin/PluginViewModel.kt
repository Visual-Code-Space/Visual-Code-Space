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

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teixeira.vcspace.plugins.PluginLoader
import com.teixeira.vcspace.plugins.internal.PluginInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PluginViewModel : ViewModel() {
  private val _installedPlugins = MutableStateFlow(mutableListOf<PluginInfo>())
  val installedPlugins = _installedPlugins.asStateFlow()

  fun loadInstalledPlugins(
    context: Context,
    onSuccessfullyLoaded: suspend CoroutineScope.() -> Unit = {},
    onError: suspend CoroutineScope.(exception: Throwable) -> Unit = {}
  ) {
    viewModelScope.launch(Dispatchers.IO) {
      runCatching {
        _installedPlugins.update {
          PluginLoader.loadPlugins(context).map { it.first }.toMutableList()
        }
      }.onSuccess { onSuccessfullyLoaded() }.onFailure { onError(it) }
    }
  }
}
