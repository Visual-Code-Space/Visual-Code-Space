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

package com.teixeira.vcspace.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val FormatLetterSpacing: ImageVector
  get() {
    if (_FormatLetterSpacing != null) {
      return _FormatLetterSpacing!!
    }
    _FormatLetterSpacing = ImageVector.Builder(
      name = "FormatLetterSpacing",
      defaultWidth = 24.dp,
      defaultHeight = 24.dp,
      viewportWidth = 960f,
      viewportHeight = 960f
    ).apply {
      path(fill = SolidColor(Color(0xFF000000))) {
        moveTo(320f, 880f)
        lineTo(160f, 720f)
        lineTo(320f, 560f)
        lineTo(377f, 616f)
        lineTo(313f, 680f)
        lineTo(647f, 680f)
        lineTo(584f, 616f)
        lineTo(640f, 560f)
        lineTo(800f, 720f)
        lineTo(640f, 880f)
        lineTo(583f, 824f)
        lineTo(647f, 760f)
        lineTo(313f, 760f)
        lineTo(376f, 824f)
        lineTo(320f, 880f)
        close()
        moveTo(200f, 480f)
        lineTo(200f, 80f)
        lineTo(280f, 80f)
        lineTo(280f, 480f)
        lineTo(200f, 480f)
        close()
        moveTo(440f, 480f)
        lineTo(440f, 80f)
        lineTo(520f, 80f)
        lineTo(520f, 480f)
        lineTo(440f, 480f)
        close()
        moveTo(680f, 480f)
        lineTo(680f, 80f)
        lineTo(760f, 80f)
        lineTo(760f, 480f)
        lineTo(680f, 480f)
        close()
      }
    }.build()

    return _FormatLetterSpacing!!
  }

@Suppress("ObjectPropertyName")
private var _FormatLetterSpacing: ImageVector? = null
