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

import com.teixeira.vcspace.preferences.PREF_ABOUT_GITHUB_KEY
import com.teixeira.vcspace.preferences.PREF_CONFIGURE_EDITOR_KEY
import com.teixeira.vcspace.preferences.PREF_CONFIGURE_FILE_KEY
import com.teixeira.vcspace.preferences.PREF_CONFIGURE_GENERAL_KEY

object PreferenceKeys {
  const val GENERAL_PREFERENCES = PREF_CONFIGURE_GENERAL_KEY
  const val EDITOR_PREFERENCES = PREF_CONFIGURE_EDITOR_KEY
  const val FILE_PREFERENCES = PREF_CONFIGURE_FILE_KEY

  const val GITHUB_ABOUT_PREFERENCES = PREF_ABOUT_GITHUB_KEY
}