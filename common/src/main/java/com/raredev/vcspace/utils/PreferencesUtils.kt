package com.raredev.vcspace.utils

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate.*
import com.raredev.vcspace.app.BaseApplication
import com.raredev.vcspace.res.R

object PreferencesUtils {

  val prefs: SharedPreferences = BaseApplication.getInstance().getPrefs()

  // General
  val appTheme: Int = when (prefs.getString(SharedPreferencesKeys.KEY_THEME, "")) {
    "light" -> MODE_NIGHT_NO
    "dark" -> MODE_NIGHT_YES
    else -> MODE_NIGHT_FOLLOW_SYSTEM
  }

  val dynamicColors: Boolean = prefs.getBoolean(SharedPreferencesKeys.KEY_DYNAMIC_COLORS, true)

  // Editor
  val textSize: Int = prefs.getInt(SharedPreferencesKeys.KEY_EDITOR_TEXT_SIZE, 14)

  val tabSize: Int = prefs.getString(SharedPreferencesKeys.KEY_EDITOR_TAB_SIZE, "4")!!.toInt()

  val selectedFont: Int = when (prefs.getString(SharedPreferencesKeys.KEY_EDITOR_FONT, "firacode")) {
    "firacode" -> R.font.firacode_regular
    "jetbrains" -> R.font.jetbrains_mono
    else -> R.font.firacode_regular
  }

  val stickyScroll: Boolean = prefs.getBoolean(SharedPreferencesKeys.KEY_STICKYSCROLL, false)

  val fontLigatures: Boolean = prefs.getBoolean(SharedPreferencesKeys.KEY_FONTLIGATURES, true)

  val wordWrap: Boolean = prefs.getBoolean(SharedPreferencesKeys.KEY_WORDWRAP, false)

  val lineNumbers: Boolean = prefs.getBoolean(SharedPreferencesKeys.KEY_LINENUMBERS, true)

  val useTab: Boolean = prefs.getBoolean(SharedPreferencesKeys.KEY_USE_TAB, false)

  val deleteEmptyLineFast: Boolean =
    prefs.getBoolean(SharedPreferencesKeys.KEY_DELETE_EMPTY_LINE_FAST, true)

  val deleteMultiSpaces: Boolean = prefs.getBoolean(SharedPreferencesKeys.KEY_DELETETABS, true)

  val identationString: String = if (useTab) "\t" else " ".repeat(tabSize)

  val autoSave: Boolean = prefs.getBoolean(SharedPreferencesKeys.KEY_AUTO_SAVE, false)

  // File
  val showHiddenFiles: Boolean = prefs.getBoolean(SharedPreferencesKeys.KEY_SHOW_HIDDEN_FILES, true)
}
