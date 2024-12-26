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

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Redo
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.ElectricalServices
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.KeyboardCommandKey
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.SaveAs
import androidx.compose.material.icons.rounded.Search
import com.teixeira.vcspace.app.strings
import com.teixeira.vcspace.core.menu.MenuItem

class MenuManager private constructor() {
  companion object {
    @JvmStatic
    val instance by lazy { MenuManager() }
  }

  private val _menuItems = mutableListOf<MenuItem>()
  val menuItems: List<MenuItem>
    get() = _menuItems

  private val _actionMenuItems = mutableListOf<MenuItem>()
  val actionMenuItems: List<MenuItem>
    get() = _actionMenuItems

  private val _fileMenuItems = mutableListOf<MenuItem>()
  val fileMenuItems: List<MenuItem>
    get() = _fileMenuItems

  fun addMenu(menuItem: MenuItem) {
    _menuItems.add(menuItem)
  }

  internal fun loadDefaultActionMenu(context: Context, onClick: (MenuItem) -> Unit) {
    _actionMenuItems.addAll(
      listOf(
        MenuItem(
          title = context.getString(strings.execute),
          id = 0,
          icon = Icons.Rounded.PlayArrow
        ).apply { this.onClick = { onClick(this) } },
        MenuItem(
          title = context.getString(strings.editor_undo),
          id = 1,
          icon = Icons.AutoMirrored.Rounded.Undo
        ).apply { this.onClick = { onClick(this) } },
        MenuItem(
          title = context.getString(strings.editor_redo),
          id = 2,
          icon = Icons.AutoMirrored.Rounded.Redo
        ).apply { this.onClick = { onClick(this) } }
      )
    )
  }

  internal fun loadDefaultMenu(context: Context, onClick: (MenuItem) -> Unit) {
    _menuItems.addAll(
      listOf(
        MenuItem(
          title = context.getString(strings.editor_search),
          id = 0,
          icon = Icons.Rounded.Search,
          shortcut = "Ctrl+K"
        ).apply { this.onClick = { onClick(this) } },
        MenuItem(
          title = "Command Palette",
          id = 1,
          icon = Icons.Rounded.KeyboardCommandKey,
          shortcut = "Ctrl+Shift+P"
        ).apply { this.onClick = { onClick(this) } },
        MenuItem(
          title = context.getString(strings.file),
          id = 2,
          icon = Icons.Rounded.Folder,
          trailingIcon = Icons.Rounded.ChevronRight
        ).apply { this.onClick = { onClick(this) } },
        MenuItem(
          title = context.getString(strings.reload_plugins),
          id = 3,
          icon = Icons.Rounded.ElectricalServices,
        ).apply { this.onClick = { onClick(this) } },
      )
    )
  }

  internal fun loadDefaultFileMenu(context: Context, onClick: (MenuItem) -> Unit) {
    _fileMenuItems.addAll(
      listOf(
        MenuItem(
          title = context.getString(strings.file_new),
          id = 0,
          icon = Icons.Rounded.Add,
          shortcut = "Ctrl+N"
        ).apply { this.onClick = { onClick(this) } },
        MenuItem(
          title = context.getString(strings.file_open),
          id = 1,
          icon = Icons.Rounded.FileOpen,
          shortcut = "Ctrl+O"
        ).apply { this.onClick = { onClick(this) } },
        MenuItem(
          title = context.getString(strings.file_save),
          id = 2,
          icon = Icons.Rounded.Save,
          shortcut = "Ctrl+S"
        ).apply { this.onClick = { onClick(this) } },
        MenuItem(
          title = context.getString(strings.file_save_as),
          id = 3,
          icon = Icons.Rounded.SaveAs,
          enabled = false
        ).apply { this.onClick = { onClick(this) } },
        MenuItem(
          title = context.getString(strings.file_saved_all),
          id = 4,
          icon = Icons.Rounded.Save,
          shortcut = "Ctrl+Shift+S"
        ).apply { this.onClick = { onClick(this) } },
        MenuItem(
          title = context.getString(strings.file_reload),
          id = 5,
          icon = Icons.Rounded.Refresh,
        ).apply { this.onClick = { onClick(this) } },
      )
    )
  }
}
