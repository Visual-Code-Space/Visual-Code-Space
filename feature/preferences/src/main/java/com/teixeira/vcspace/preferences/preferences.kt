package com.teixeira.vcspace.preferences

import androidx.appcompat.app.AppCompatDelegate
import com.teixeira.vcspace.app.BaseApplication
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.utils.isDarkMode

const val PREF_CONFIGURE_GENERAL_KEY = "pref_configure_general_key"
const val PREF_CONFIGURE_EDITOR_KEY = "pref_configure_editor_key"
const val PREF_CONFIGURE_FILE_KEY = "pref_configure_file_key"
const val PREF_ABOUT_GITHUB_KEY = "pref_about_github_key"

// General
val PREF_APARENCE_UIMODE_KEY = "pref_aparence_uimode_key"
val PREF_APARENCE_MATERIALYOU_KEY = "pref_aparence_materialyou_key"

// Editor
val PREF_EDITOR_FONTSIZE_KEY = "pref_editor_fontsize_key"
val PREF_EDITOR_INDENT_KEY = "pref_editor_indent_key"
val PREF_EDITOR_FONT_KEY = "pref_editor_font_key"
val PREF_EDITOR_COLORSCHEME_KEY = "pref_editor_colorscheme_key"
val PREF_EDITOR_STICKYSCROLL_KEY = "pref_editor_stickyscroll_key"
val PREF_EDITOR_FONTLIGATURES_KEY = "pref_editor_fontligatures_key"
val PREF_EDITOR_WORDWRAP_KEY = "pref_editor_wordwrap_key"
val PREF_EDITOR_LINENUMBER_KEY = "pref_editor_linenumber_key"
val PREF_EDITOR_USETAB_KEY = "pref_editor_usetab_key"
val PREF_EDITOR_DELETELINEONBACKSPACE_KEY = "pref_editor_deletelineonbackspace"
val PREF_EDITOR_DELETETABONBACKSPACE_KEY = "pref_editor_deletetabonbackspace"
val PREF_EDITOR_TABS_AUTOSAVE = "pref_editor_tabs_autosave"

// File
val PREF_FILE_SHOWHIDDENFILES_KEY = "pref_file_showhiddenfiles_key"

val app = BaseApplication.instance
val defaultPrefs = app.defaultPrefs

// General
val aparenceUIMode: Int
  get() =
    when (defaultPrefs.getInt(PREF_APARENCE_UIMODE_KEY, 0)) {
      1 -> AppCompatDelegate.MODE_NIGHT_NO
      2 -> AppCompatDelegate.MODE_NIGHT_YES
      else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

val aparenceMaterialYou: Boolean
  get() = defaultPrefs.getBoolean(PREF_APARENCE_MATERIALYOU_KEY, true)

// Editor
val editorFontSize: Float
  get() = defaultPrefs.getFloat(PREF_EDITOR_FONTSIZE_KEY, 14f)

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
