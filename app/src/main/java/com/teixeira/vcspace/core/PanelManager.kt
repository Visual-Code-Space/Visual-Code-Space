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

package com.teixeira.vcspace.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import com.vcspace.plugins.panel.ComposeFactory
import com.vcspace.plugins.panel.Panel

class PanelManager private constructor() {
  companion object {
    @JvmStatic
    val instance by lazy { PanelManager() }
  }

  private val _panels = mutableStateMapOf<String, Panel>()
  val panels get() = _panels.toMap()

  fun addPanel(id: String, title: String, factory: ComposeFactory) = Panel(id, title, factory).also {
    _panels[id] = it
  }

  fun getPanelById(id: String) = _panels[id]

  fun removePanel(id: String) = _panels.remove(id)
}
