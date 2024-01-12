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

package com.raredev.vcspace.utils

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate.*
import com.raredev.vcspace.app.BaseApplication
import com.raredev.vcspace.res.R

object PreferencesUtils {

  val prefs: SharedPreferences
    get() = BaseApplication.getInstance().getPrefs()

  // General
  val appTheme: Int
    get() = when (prefs.getString(SharedPreferencesKeys.KEY_THEME, "")) {
      "light" -> MODE_NIGHT_NO
      "dark" -> MODE_NIGHT_YES
      else -> MODE_NIGHT_FOLLOW_SYSTEM
    }

  val dynamicColors: Boolean
    get() = prefs.getBoolean(SharedPreferencesKeys.KEY_DYNAMIC_COLORS, true)

  // Editor
  val textSize: Int
    get() = prefs.getInt(SharedPreferencesKeys.KEY_EDITOR_TEXT_SIZE, 14)

  val tabSize: Int
    get() = prefs.getString(SharedPreferencesKeys.KEY_EDITOR_TAB_SIZE, "4")!!.toInt()

  val selectedFont: Int
    get() = when (prefs.getString(SharedPreferencesKeys.KEY_EDITOR_FONT, "firacode")) {
      "firacode" -> R.font.firacode_regular
      "jetbrains" -> R.font.jetbrains_mono
      else -> R.font.firacode_regular
    }

  val stickyScroll: Boolean
    get() = prefs.getBoolean(SharedPreferencesKeys.KEY_STICKY_SCROLL, false)

  val fontLigatures: Boolean
    get() = prefs.getBoolean(SharedPreferencesKeys.KEY_FONT_LIGATURES, true)

  val wordWrap: Boolean
    get() = prefs.getBoolean(SharedPreferencesKeys.KEY_WORDWRAP, false)

  val lineNumbers: Boolean
    get() = prefs.getBoolean(SharedPreferencesKeys.KEY_LINE_NUMBERS, true)

  val useTab: Boolean
    get() = prefs.getBoolean(SharedPreferencesKeys.KEY_USE_TAB, false)

  val deleteEmptyLineFast: Boolean
    get() = prefs.getBoolean(SharedPreferencesKeys.KEY_DELETE_EMPTY_LINE_FAST, true)

  val deleteMultiSpaces: Boolean = prefs.getBoolean(SharedPreferencesKeys.KEY_DELETE_TABS, true)

  val identationString: String
    get() = if (useTab) "\t" else " ".repeat(tabSize)

  val autoSave: Boolean
    get() = prefs.getBoolean(SharedPreferencesKeys.KEY_AUTO_SAVE, false)

  // File
  val showHiddenFiles: Boolean
    get() = prefs.getBoolean(SharedPreferencesKeys.KEY_SHOW_HIDDEN_FILES, true)

  val isPythonFileExtracted: Boolean
    get() = prefs.getBoolean(SharedPreferencesKeys.KEY_PYTHON_FILE_EXTRACTED, false)
}
