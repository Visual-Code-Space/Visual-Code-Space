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

val APP_EXTERNAL_DIR = "${PathUtils.getExternalStoragePath()}/VCSpace"

const val ORGANIZATION_NAME = "Visual-Code-Space"
const val APPLICATION_REPOSITORY_NAME = "Visual-Code-Space"

const val KEY_GIT_USERNAME = "git_username"
const val KEY_GIT_PASSWORD = "git_password"
const val KEY_GIT_USER_INFO = "git_user_info"
const val KEY_GIT_USER_ACCESS_TOKEN = "git_user_access_token"

object PreferenceKeys {
  const val RECENT_FOLDER = "recent_folder"

  const val PLUGINS_PATH = "plugins_path"
}

object PluginConstants {
  val PLUGIN_HOME = "$APP_EXTERNAL_DIR/plugins"
}