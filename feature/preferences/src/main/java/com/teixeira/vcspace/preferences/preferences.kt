package com.teixeira.vcspace.preferences

import androidx.appcompat.app.AppCompatDelegate
import com.teixeira.vcspace.PluginConstants
import com.teixeira.vcspace.PreferenceKeys
import com.teixeira.vcspace.app.BaseApplication
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.utils.isDarkMode

// General
const val PREF_APPEARANCE_UI_MODE_KEY = "pref_aparence_uimode_key"
const val PREF_APPEARANCE_MATERIAL_YOU_KEY = "pref_aparence_materialyou_key"
const val PREF_REMEMBER_LAST_OPENED_FILE = "pref_remember_last_opened_file"

// Editor
const val PREF_EDITOR_FONT_SIZE_KEY = "pref_editor_fontsize_key"
const val PREF_EDITOR_INDENT_KEY = "pref_editor_indent_key"
const val PREF_EDITOR_FONT_KEY = "pref_editor_font_key"
const val PREF_EDITOR_COLORSCHEME_KEY = "pref_editor_colorscheme_key"
const val PREF_EDITOR_STICKYSCROLL_KEY = "pref_editor_stickyscroll_key"
const val PREF_EDITOR_FONTLIGATURES_KEY = "pref_editor_fontligatures_key"
const val PREF_EDITOR_WORDWRAP_KEY = "pref_editor_wordwrap_key"
const val PREF_EDITOR_LINENUMBER_KEY = "pref_editor_linenumber_key"
const val PREF_EDITOR_USETAB_KEY = "pref_editor_usetab_key"
const val PREF_EDITOR_DELETELINEONBACKSPACE_KEY = "pref_editor_deletelineonbackspace"
const val PREF_EDITOR_DELETETABONBACKSPACE_KEY = "pref_editor_deletetabonbackspace"
const val PREF_EDITOR_TABS_AUTOSAVE = "pref_editor_tabs_autosave"

// File
const val PREF_FILE_SHOWHIDDENFILES_KEY = "pref_file_showhiddenfiles_key"

val app = BaseApplication.instance
val defaultPrefs = app.defaultPrefs

// General
val appearanceUIMode: Int
  get() =
    when (defaultPrefs.getInt(PREF_APPEARANCE_UI_MODE_KEY, 0)) {
      1 -> AppCompatDelegate.MODE_NIGHT_NO
      2 -> AppCompatDelegate.MODE_NIGHT_YES
      else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

val isLocalDarkTheme get() = appearanceUIMode == AppCompatDelegate.MODE_NIGHT_YES
val isLocalFollowSystemTheme get() = appearanceUIMode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

val appearanceMaterialYou: Boolean
  get() = defaultPrefs.getBoolean(PREF_APPEARANCE_MATERIAL_YOU_KEY, true)

val rememberLastOpenedFile: Boolean
  get() = defaultPrefs.getBoolean(PREF_REMEMBER_LAST_OPENED_FILE, false)

// Editor
val editorFontSize: Float
  get() = defaultPrefs.getFloat(PREF_EDITOR_FONT_SIZE_KEY, 14f)

val editorIndent: Int
  get() =
    when (defaultPrefs.getInt(PREF_EDITOR_INDENT_KEY, 1)) {
      0 -> 2
      1 -> 4
      2 -> 6
      3 -> 8
      else -> 4
    }

val editorFont: Int
  get() =
    when (defaultPrefs.getInt(PREF_EDITOR_FONT_KEY, 0)) {
      0 -> R.font.firacode_regular
      1 -> R.font.jetbrains_mono
      else -> R.font.firacode_regular
    }

val editorColorScheme: String
  get() =
    when (defaultPrefs.getInt(PREF_EDITOR_COLORSCHEME_KEY, 0)) {
      0 -> editorUIModeScheme
      1 -> "quietlight"
      2 -> "darcula"
      3 -> "abyss"
      4 -> "solarized_drak"
      5 -> "pythondm"
      else -> editorUIModeScheme
    }

private val editorUIModeScheme: String
  get() = if (app.isDarkMode()) "darcula" else "quietlight"

val editorStickyScroll: Boolean
  get() = defaultPrefs.getBoolean(PREF_EDITOR_STICKYSCROLL_KEY, false)

val editorFontLigatures: Boolean
  get() = defaultPrefs.getBoolean(PREF_EDITOR_FONTLIGATURES_KEY, true)

val editorWordWrap: Boolean
  get() = defaultPrefs.getBoolean(PREF_EDITOR_WORDWRAP_KEY, false)

val editorLineNumber: Boolean
  get() = defaultPrefs.getBoolean(PREF_EDITOR_LINENUMBER_KEY, true)

val editorUseTab: Boolean
  get() = defaultPrefs.getBoolean(PREF_EDITOR_USETAB_KEY, false)

val editorDeleteLineOnBackspace: Boolean
  get() = defaultPrefs.getBoolean(PREF_EDITOR_DELETELINEONBACKSPACE_KEY, true)

val editorDeleteTabOnBackspace: Boolean
  get() = defaultPrefs.getBoolean(PREF_EDITOR_DELETETABONBACKSPACE_KEY, true)

val editorTabsAutosave: Boolean
  get() = defaultPrefs.getBoolean(PREF_EDITOR_TABS_AUTOSAVE, false)

// File
val fileShowhiddenfiles: Boolean
  get() = defaultPrefs.getBoolean(PREF_FILE_SHOWHIDDENFILES_KEY, true)

// Others
const val PREF_PYTHON_EXTRACTED_KEY = "pref_python_extracted_key"
const val PREF_PYTHON_DOWNLOADED_KEY = "pref_python_downloaded_key"

var pythonExtracted: Boolean
  get() = defaultPrefs.getBoolean(PREF_PYTHON_EXTRACTED_KEY, false)
  set(value) {
    defaultPrefs.edit().putBoolean(PREF_PYTHON_EXTRACTED_KEY, value).apply()
  }

var pythonDownloaded: Boolean
  get() = defaultPrefs.getBoolean(PREF_PYTHON_DOWNLOADED_KEY, false)
  set(value) {
    defaultPrefs.edit().putBoolean(PREF_PYTHON_DOWNLOADED_KEY, value).apply()
  }

var pluginsPath: String
  get() = defaultPrefs.getString(PreferenceKeys.PLUGINS_PATH, PluginConstants.PLUGIN_HOME)!!
  set(value) = defaultPrefs.edit().putString(PreferenceKeys.PLUGINS_PATH, value).apply()
