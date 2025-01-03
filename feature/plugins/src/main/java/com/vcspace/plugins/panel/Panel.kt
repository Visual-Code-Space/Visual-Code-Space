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

package com.vcspace.plugins.panel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset

data class Panel(val id: String, val title: String, val factory: ComposeFactory) {
  private var _offset = mutableStateOf(Offset.Zero)
  var offset: Offset by _offset

  private var _isVisible = mutableStateOf(false)
  val isVisible by _isVisible

  fun show() {
    _isVisible.value = true
  }

  fun hide() {
    _isVisible.value = false
  }
}
