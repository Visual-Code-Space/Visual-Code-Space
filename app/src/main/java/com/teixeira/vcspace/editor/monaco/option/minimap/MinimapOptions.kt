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

package com.teixeira.vcspace.editor.monaco.option.minimap

import com.google.gson.Gson

data class MinimapOptions(
  val enabled: Boolean? = null,
  val autohide: Boolean? = null,
  val side: Side? = null,
  val size: Size? = null,
  val showSlider: ShowSlider? = null,
  val renderCharacters: Boolean? = null,
  val maxColumn: Number? = null,
  val scale: Number? = null,
  val showRegionSectionHeaders: Boolean? = null,
  val showMarkSectionHeaders: Boolean? = null,
  val sectionHeaderFontSize: Number? = null,
  val sectionHeaderLetterSpacing: Number? = null
) {
  fun toJson(): String {
    return Gson().toJson(this)
  }
}
