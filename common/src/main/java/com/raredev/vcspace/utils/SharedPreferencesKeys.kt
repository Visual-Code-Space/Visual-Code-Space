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

object SharedPreferencesKeys {
  // General
  const val KEY_GENERAL = "pref_general"
  const val KEY_THEME = "pref_theme"
  const val KEY_DYNAMIC_COLORS = "pref_dynamiccolors"

  // Editor
  const val KEY_EDITOR = "pref_editor"
  const val KEY_EDITOR_TEXT_SIZE = "pref_font_size"
  const val KEY_EDITOR_TAB_SIZE = "pref_editortabsize"
  const val KEY_EDITOR_FONT = "pref_editorfont"
  const val KEY_STICKYSCROLL = "pref_stickyscroll"
  const val KEY_FONTLIGATURES = "pref_fontligatures"
  const val KEY_WORDWRAP = "pref_wordwrap"
  const val KEY_LINENUMBERS = "pref_linenumbers"
  const val KEY_USE_TAB = "pref_usetab"
  const val KEY_DELETE_EMPTY_LINE_FAST = "pref_deleteemptylinefast"
  const val KEY_DELETETABS = "pref_deletetabs"
  const val KEY_AUTO_SAVE = "pref_auto_save"

  // File
  const val KEY_FILE = "pref_file"
  const val KEY_SHOW_HIDDEN_FILES = "pref_show_hidden_files"

  // Git
  const val KEY_GIT = "pref_git"
  const val KEY_CREDENTIAL = "pref_credential"
  const val KEY_CREDENTIAL_USERNAME = "credential_username"
  const val KEY_CREDENTIAL_PASSWORD = "credential_password"

  // Others
  const val KEY_GITHUB = "github"
  const val KEY_LICENSES = "licenses"
}