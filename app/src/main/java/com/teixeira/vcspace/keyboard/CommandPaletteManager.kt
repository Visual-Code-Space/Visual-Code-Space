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

package com.teixeira.vcspace.keyboard

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.teixeira.vcspace.keyboard.model.Command
import com.teixeira.vcspace.keyboard.model.Command.Companion.toKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class CommandPaletteManager {
  companion object {
    @JvmStatic
    val instance by lazy { CommandPaletteManager() }

    private const val COMMANDS_FILE_NAME = "recently_used_commands.json"
  }

  init {
    // loadRecentlyUsedCommands()
  }

  private val _showCommandPalette = mutableStateOf(false)
  val showCommandPalette get() = _showCommandPalette

  fun show() {
    _showCommandPalette.value = true
  }

  fun hide() {
    _showCommandPalette.value = false
  }

  private val _allCommands = mutableListOf<Command>()
  val allCommands get() = _allCommands.toList()

  fun addCommand(vararg command: Command) {
    _allCommands.addAll(command)
  }

  private var _recentlyUsedCommands = mutableListOf<Command>()
  val recentlyUsedCommands get() = _recentlyUsedCommands.toList()

  fun addRecentlyUsedCommand(command: Command) {
    _recentlyUsedCommands.remove(command)

    // Add the command to the top of the list
    _recentlyUsedCommands.add(0, command)

    if (_recentlyUsedCommands.size > 10) {
      _recentlyUsedCommands.removeAt(_recentlyUsedCommands.lastIndex) // Remove the oldest command
    }

    // saveRecentlyUsedCommands()
  }

  private fun saveRecentlyUsedCommands() {
    val json = Gson().toJson(_recentlyUsedCommands)
    val filesDir = PathUtils.getFilesPathExternalFirst()
    File("$filesDir/configs/$COMMANDS_FILE_NAME").apply {
      FileUtils.createOrExistsFile(this)
      writeText(json)
    }
  }

  private fun loadRecentlyUsedCommands() {
    val filesDir = PathUtils.getFilesPathExternalFirst()
    val file = File("$filesDir/configs/$COMMANDS_FILE_NAME")

    if (file.exists()) {
      val json = file.readText()
      val type = object : TypeToken<List<Command>>() {}.type

      val commands: List<Command> = Gson().fromJson(json, type)
      _recentlyUsedCommands.clear()
      _recentlyUsedCommands.addAll(commands)
    }
  }

  fun applyKeyBindings(event: KeyEvent) {
    val pressedKey = event.key

    val isCtrlPressed = event.isCtrlPressed
    val isShiftPressed = event.isShiftPressed
    val isAltPressed = event.isAltPressed

    for (command in allCommands) {
      val keyBinding = command.keybinding
      val keys = keyBinding.split("+")

      var isCtrlRequired = false
      var isShiftRequired = false
      var isAltRequired = false
      var bindingKey: Key? = null

      for (key in keys) {
        when (key) {
          "Ctrl" -> isCtrlRequired = true
          "Shift" -> isShiftRequired = true
          "Alt" -> isAltRequired = true
          else -> bindingKey = key.toKey() // This is the actual key (e.g., "P", "V", etc.)
        }
      }

      if (isCtrlPressed == isCtrlRequired &&
        isShiftPressed == isShiftRequired &&
        isAltPressed == isAltRequired &&
        pressedKey == bindingKey
      ) {
        println("Keybinding '${keyBinding}' is pressed")
        command.action(command)
      }
    }
  }
}