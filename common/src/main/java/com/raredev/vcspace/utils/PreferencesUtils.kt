package com.raredev.vcspace.utils

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.raredev.vcspace.app.BaseApplication.Companion.getInstance
import com.raredev.vcspace.res.R

object PreferencesUtils {

  val prefs: SharedPreferences
    get() = getInstance().getPrefs()

  // General
  val appTheme: Int
    get() =
        when (prefs.getString(SharedPreferencesKeys.KEY_THEME, "")) {
          "light" -> AppCompatDelegate.MODE_NIGHT_NO
          "dark" -> AppCompatDelegate.MODE_NIGHT_YES
          else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

  val dynamicColors: Boolean
    get() = prefs.getBoolean(SharedPreferencesKeys.KEY_DYNAMIC_COLORS, true)

  // Editor
  val textSize: Int
    get() = prefs.getInt(SharedPreferencesKeys.KEY_EDITOR_TEXT_SIZE, 14)

  val tabSize: Int
    get() = prefs.getString(SharedPreferencesKeys.KEY_EDITOR_TAB_SIZE, "4")!!.toInt()

  val selectedFont: Int
    get() =
        when (prefs.getString(SharedPreferencesKeys.KEY_EDITOR_FONT, "firacode")) {
          "firacode" -> R.font.firacode_regular
          "jetbrains" -> R.font.jetbrains_mono
          else -> R.font.firacode_regular
        }

  val stickyScroll: Boolean
    get() = prefs.getBoolean(SharedPreferencesKeys.KEY_STICKYSCROLL, false)

  val fontLigatures: Boolean
    get() = prefs.getBoolean(SharedPreferencesKeys.KEY_FONTLIGATURES, true)

  val wordWrap: Boolean
    get() = prefs.getBoolean(SharedPreferencesKeys.KEY_WORDWRAP, false)

  val lineNumbers: Boolean
    get() = prefs.getBoolean(SharedPreferencesKeys.KEY_LINENUMBERS, true)

  val useTab: Boolean
    get() = prefs.getBoolean(SharedPreferencesKeys.KEY_USE_TAB, false)

  val deleteEmptyLineFast: Boolean
    get() = prefs.getBoolean(SharedPreferencesKeys.KEY_DELETE_EMPTY_LINE_FAST, true)

  val deleteMultiSpaces: Boolean
    get() = prefs.getBoolean(SharedPreferencesKeys.KEY_DELETETABS, true)

  val identationString: String
    get() = if (useTab) "\t" else " ".repeat(tabSize)

  val autoSave: Boolean
    get() = prefs.getBoolean(SharedPreferencesKeys.KEY_AUTO_SAVE, false)

  // File
  val showHiddenFiles: Boolean
    get() = prefs.getBoolean(SharedPreferencesKeys.KEY_SHOW_HIDDEN_FILES, true)
}
