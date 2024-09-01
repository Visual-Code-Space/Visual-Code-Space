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

package com.teixeira.vcspace

import com.blankj.utilcode.util.PathUtils

// From https://github.com/PsiCodes/ktxpy
const val PYTHON_PACKAGE_URL_64_BIT =
  "https://github.com/PsiCodes/ktxpy/raw/master/app/arch_arm64-v8a/assets/python.7z"
const val PYTHON_PACKAGE_URL_32_BIT =
  "https://github.com/PsiCodes/ktxpy/raw/master/app/arch_arm32/assets/python.7z"

object PreferenceKeys {
  const val GENERAL_PREFERENCES = "pref_configure_general_key"
  const val EDITOR_PREFERENCES = "pref_configure_editor_key"
  const val FILE_PREFERENCES = "pref_configure_file_key"

  const val PLUGINS_PREFERENCES = "pref_configure_plugins_key"

  const val GITHUB_ABOUT_PREFERENCES = "pref_about_github_key"
}

object PluginConstants {
  val PLUGIN_HOME = "${PathUtils.getExternalAppFilesPath()}/plugins"
}