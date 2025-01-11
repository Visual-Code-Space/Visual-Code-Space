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
import com.itsvks.monaco.MonacoTheme
import com.itsvks.monaco.option.AcceptSuggestionOnEnter
import com.itsvks.monaco.option.MatchBrackets
import com.itsvks.monaco.option.TextEditorCursorBlinkingStyle
import com.itsvks.monaco.option.TextEditorCursorStyle
import com.itsvks.monaco.option.WordBreak
import com.itsvks.monaco.option.WordWrap
import com.itsvks.monaco.option.WrappingStrategy
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
    val FOLLOW_SYSTEM_THEME = booleanPreferencesKey("follow_system_theme")
    val DARK_MODE = booleanPreferencesKey("dark_mode")
    val AMOLED_MODE = booleanPreferencesKey("amoled_mode")
    val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
    val ENABLE_GESTURE_IN_DRAWER = booleanPreferencesKey("enable_gesture_in_drawer")

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

    @Composable
    fun rememberEnableGestureInDrawer() = rememberPreference(
      key = ENABLE_GESTURE_IN_DRAWER,
      defaultValue = true
    )
  }

  object File {
    val SHOW_HIDDEN_FILES = booleanPreferencesKey("show_hidden_files")
    val REMEMBER_LAST_OPENED_FILE = booleanPreferencesKey("__remember_last_opened_file__")

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
    val CURRENT_EDITOR = stringPreferencesKey("current_editor")
    val SHOW_INPUT_METHOD_PICKER_AT_START =
      booleanPreferencesKey("show_input_method_picker_at_start")

    val FONT_SIZE = floatPreferencesKey("font_size")
    val INDENT_SIZE = intPreferencesKey("indent_size")
    val FONT_FAMILY = stringPreferencesKey("font_family")
    val COLOR_SCHEME = stringPreferencesKey("color_scheme")
    val STICKY_SCROLL = booleanPreferencesKey("sticky_scroll")
    val FONT_LIGATURES = booleanPreferencesKey("font_ligatures")
    val SYMBOLS = stringPreferencesKey("symbols")
    val WORD_WRAP = booleanPreferencesKey("word_wrap")
    val LINE_NUMBER = booleanPreferencesKey("line_number")
    val USE_TAB = booleanPreferencesKey("use_tab")
    val DELETE_LINE_ON_BACKSPACE = booleanPreferencesKey("delete_line_on_backspace")
    val DELETE_INDENT_ON_BACKSPACE = booleanPreferencesKey("delete_indent_on_backspace")
    val EDITOR_TEXT_ACTION_WINDOW_EXPAND_THRESHOLD =
      intPreferencesKey("editor_text_action_window_expand_threshold")

    @Composable
    fun rememberCurrentEditor() = rememberPreference(key = CURRENT_EDITOR, defaultValue = "Sora")

    @Composable
    fun rememberShowInputMethodPickerAtStart() =
      rememberPreference(key = SHOW_INPUT_METHOD_PICKER_AT_START, defaultValue = false)

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
    fun rememberSymbols() =
      rememberPreference(key = SYMBOLS, defaultValue = "!@#$%^&*()_+{}:\"<>?;=-[]\\/.,")

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

    @Composable
    fun rememberEditorTextActionWindowExpandThreshold() = rememberPreference(
      key = EDITOR_TEXT_ACTION_WINDOW_EXPAND_THRESHOLD,
      defaultValue = 10
    )
  }

  object EditorTabs {
    val AUTO_SAVE = booleanPreferencesKey("auto_save")

    @Composable
    fun rememberAutoSave() = rememberPreference(key = AUTO_SAVE, defaultValue = false)
  }

  object Monaco {
    val MONACO_THEME = stringPreferencesKey("monaco_theme")
    val FONT_SIZE = intPreferencesKey("monaco_font_size")
    val LINE_NUMBERS_MIN_CHARS = intPreferencesKey("line_numbers_min_chars")
    val LINE_DECORATIONS_WIDTH = intPreferencesKey("line_decorations_width")
    val LETTER_SPACING = floatPreferencesKey("letter_spacing")
    val MATCH_BRACKETS = stringPreferencesKey("match_brackets")
    val ACCEPT_SUGGESTION_ON_COMMIT_CHARACTER =
      booleanPreferencesKey("accept_suggestion_on_commit_character")
    val ACCEPT_SUGGESTION_ON_ENTER = stringPreferencesKey("accept_suggestion_on_enter")
    val FOLDING = booleanPreferencesKey("folding")
    val GLYPH_MARGIN = booleanPreferencesKey("glyph_margin")
    val WORD_WRAP = stringPreferencesKey("monaco_word_wrap")
    val WORD_BREAK = stringPreferencesKey("monaco_word_break")
    val WRAPPING_STRATEGY = stringPreferencesKey("wrapping_strategy")
    val CURSOR_STYLE = intPreferencesKey("monaco_cursor_style")
    val CURSOR_BLINKING_STYLE = intPreferencesKey("monaco_cursor_blinking_style")

    @Composable
    fun rememberMonacoTheme() =
      rememberPreference(key = MONACO_THEME, defaultValue = MonacoTheme.VisualStudioDark.value)

    @Composable
    fun rememberFontSize() = rememberPreference(key = FONT_SIZE, defaultValue = 14)

    @Composable
    fun rememberLineNumbersMinChars() =
      rememberPreference(key = LINE_NUMBERS_MIN_CHARS, defaultValue = 1)

    @Composable
    fun rememberLineDecorationsWidth() =
      rememberPreference(key = LINE_DECORATIONS_WIDTH, defaultValue = 1)

    @Composable
    fun rememberLetterSpacing() = rememberPreference(key = LETTER_SPACING, defaultValue = 0f)

    @Composable
    fun rememberMatchBrackets() =
      rememberPreference(key = MATCH_BRACKETS, defaultValue = MatchBrackets.Always.value)

    @Composable
    fun rememberAcceptSuggestionOnCommitCharacter() =
      rememberPreference(key = ACCEPT_SUGGESTION_ON_COMMIT_CHARACTER, defaultValue = true)

    @Composable
    fun rememberAcceptSuggestionOnEnter() = rememberPreference(
      key = ACCEPT_SUGGESTION_ON_ENTER,
      defaultValue = AcceptSuggestionOnEnter.On.value
    )

    @Composable
    fun rememberWordWrap() = rememberPreference(key = WORD_WRAP, defaultValue = WordWrap.On.value)

    @Composable
    fun rememberWordBreak() =
      rememberPreference(key = WORD_BREAK, defaultValue = WordBreak.Normal.value)

    @Composable
    fun rememberWrappingStrategy() =
      rememberPreference(key = WRAPPING_STRATEGY, defaultValue = WrappingStrategy.Advanced.value)

    @Composable
    fun rememberCursorStyle() =
      rememberPreference(key = CURSOR_STYLE, defaultValue = TextEditorCursorStyle.Line.value)

    @Composable
    fun rememberCursorBlinkingStyle() = rememberPreference(
      key = CURSOR_BLINKING_STYLE,
      defaultValue = TextEditorCursorBlinkingStyle.Phase.value
    )

    @Composable
    fun rememberFolding() = rememberPreference(key = FOLDING, defaultValue = true)

    @Composable
    fun rememberGlyphMargin() = rememberPreference(key = GLYPH_MARGIN, defaultValue = true)
  }
}