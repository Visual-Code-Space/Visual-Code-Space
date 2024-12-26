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

import android.content.Context
import android.util.Log
import com.vcspace.plugins.command.EditorCommand
import com.vcspace.plugins.menu.MenuAction
import com.vcspace.plugins.menu.MenuItem

interface PluginContext {
  companion object {
    private const val TAG = "PluginContext"
  }

  val appContext: Context

  fun registerCommand(command: EditorCommand)

  fun addMenu(menuItem: MenuItem)

  fun addMenu(title: String, id: Int, action: MenuAction) {
    addMenu(MenuItem(title, id = id, action = action))
  }

  fun log(message: String) {
    Log.i(TAG, message)
  }
}
