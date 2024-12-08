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

val Telegram: ImageVector
  get() {
    if (_Telegram != null) {
      return _Telegram!!
    }
    _Telegram = ImageVector.Builder(
      name = "Telegram",
      defaultWidth = 24.dp,
      defaultHeight = 24.dp,
      viewportWidth = 24f,
      viewportHeight = 24f,
      autoMirror = true
    ).apply {
      path(fill = SolidColor(Color(0xFF000000))) {
        moveTo(9.78f, 18.65f)
        lineTo(10.06f, 14.42f)
        lineTo(17.74f, 7.5f)
        curveTo(18.08f, 7.19f, 17.67f, 7.04f, 17.22f, 7.31f)
        lineTo(7.74f, 13.3f)
        lineTo(3.64f, 12f)
        curveTo(2.76f, 11.75f, 2.75f, 11.14f, 3.84f, 10.7f)
        lineTo(19.81f, 4.54f)
        curveTo(20.54f, 4.21f, 21.24f, 4.72f, 20.96f, 5.84f)
        lineTo(18.24f, 18.65f)
        curveTo(18.05f, 19.56f, 17.5f, 19.78f, 16.74f, 19.36f)
        lineTo(12.6f, 16.3f)
        lineTo(10.61f, 18.23f)
        curveTo(10.38f, 18.46f, 10.19f, 18.65f, 9.78f, 18.65f)
        close()
      }
    }.build()

    return _Telegram!!
  }

@Suppress("ObjectPropertyName")
private var _Telegram: ImageVector? = null
