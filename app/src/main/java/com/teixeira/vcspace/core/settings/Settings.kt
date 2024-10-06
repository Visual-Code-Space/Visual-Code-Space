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

package com.teixeira.vcspace.core.settings

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = Settings.PREFERENCE_NAME)

@Composable
private fun <T> rememberPreference(
  key: Preferences.Key<T>,
  defaultValue: T,
): MutableState<T> {
  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current

  val state by remember {
    context.dataStore.data.map { it[key] ?: defaultValue }
  }.collectAsStateWithLifecycle(initialValue = defaultValue)

  return remember(state) {
    object : MutableState<T> {
      override var value: T
        get() = state
        set(value) {
          coroutineScope.launch {
            context.dataStore.edit {
              it[key] = value
            }
          }
        }

      override fun component1() = value
      override fun component2(): (T) -> Unit = { value = it }
    }
  }
}

object Settings {
  const val PREFERENCE_NAME = "settings"

  object General {
    private val FOLLOW_SYSTEM_THEME = booleanPreferencesKey("follow_system_theme")
    private val DARK_MODE = booleanPreferencesKey("dark_mode")
    private val AMOLED_MODE = booleanPreferencesKey("amoled_mode")
    private val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")

    @Composable
    fun rememberFollowSystemTheme() = rememberPreference(
      key = FOLLOW_SYSTEM_THEME,
      defaultValue = true
    )

    @Composable
    fun rememberIsDarkMode() = rememberPreference(key = DARK_MODE, defaultValue = false)

    @Composable
    fun rememberIsAmoledMode() = rememberPreference(key = AMOLED_MODE, defaultValue = false)

    @Composable
    fun rememberIsDynamicColor() = rememberPreference(key = DYNAMIC_COLOR, defaultValue = true)
  }

  object File {
    private val SHOW_HIDDEN_FILES = booleanPreferencesKey("show_hidden_files")
    private val REMEMBER_LAST_OPENED_FILE = booleanPreferencesKey("remember_last_opened_file")

    @Composable
    fun rememberShowHiddenFiles() = rememberPreference(
      key = SHOW_HIDDEN_FILES,
      defaultValue = false
    )

    @Composable
    fun rememberLastOpenedFile() = rememberPreference(
      key = REMEMBER_LAST_OPENED_FILE,
      defaultValue = false
    )
  }

  object Editor {
    private val FONT_SIZE = floatPreferencesKey("font_size")
    private val INDENT_SIZE = intPreferencesKey("indent_size")
    private val FONT_FAMILY = stringPreferencesKey("font_family")
    private val COLOR_SCHEME = stringPreferencesKey("color_scheme")
    private val STICKY_SCROLL = booleanPreferencesKey("sticky_scroll")
    private val FONT_LIGATURES = booleanPreferencesKey("font_ligatures")
    private val WORD_WRAP = booleanPreferencesKey("word_wrap")
    private val LINE_NUMBER = booleanPreferencesKey("line_number")
    private val USE_TAB = booleanPreferencesKey("use_tab")
    private val DELETE_LINE_ON_BACKSPACE = booleanPreferencesKey("delete_line_on_backspace")
    private val DELETE_INDENT_ON_BACKSPACE = booleanPreferencesKey("delete_indent_on_backspace")

    @Composable
    fun rememberFontSize() = rememberPreference(key = FONT_SIZE, defaultValue = 14f)

    @Composable
    fun rememberIndentSize() = rememberPreference(key = INDENT_SIZE, defaultValue = 4)

    @Composable
    fun rememberFontFamily() = rememberPreference(key = FONT_FAMILY, defaultValue = "")

    @Composable
    fun rememberColorScheme() = rememberPreference(key = COLOR_SCHEME, defaultValue = "")

    @Composable
    fun rememberStickyScroll() = rememberPreference(key = STICKY_SCROLL, defaultValue = false)

    @Composable
    fun rememberFontLigatures() = rememberPreference(key = FONT_LIGATURES, defaultValue = false)

    @Composable
    fun rememberWordWrap() = rememberPreference(key = WORD_WRAP, defaultValue = false)

    @Composable
    fun rememberLineNumber() = rememberPreference(key = LINE_NUMBER, defaultValue = true)

    @Composable
    fun rememberUseTab() = rememberPreference(key = USE_TAB, defaultValue = true)

    @Composable
    fun rememberDeleteLineOnBackspace() = rememberPreference(
      key = DELETE_LINE_ON_BACKSPACE,
      defaultValue = true
    )

    @Composable
    fun rememberDeleteIndentOnBackspace() = rememberPreference(
      key = DELETE_INDENT_ON_BACKSPACE,
      defaultValue = false
    )
  }

  object EditorTabs {
    private val AUTO_SAVE = booleanPreferencesKey("auto_save")

    @Composable
    fun rememberAutoSave() = rememberPreference(key = AUTO_SAVE, defaultValue = false)
  }
}