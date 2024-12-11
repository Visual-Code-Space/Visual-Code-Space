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

val Import: ImageVector
  get() {
    if (_Import != null) {
      return _Import!!
    }
    _Import = ImageVector.Builder(
      name = "Import",
      defaultWidth = 24.dp,
      defaultHeight = 24.dp,
      viewportWidth = 24f,
      viewportHeight = 24f
    ).apply {
      path(fill = SolidColor(Color(0xFF000000))) {
        moveTo(1f, 12f)
        horizontalLineTo(10.8f)
        lineTo(8.3f, 9.5f)
        lineTo(9.7f, 8.1f)
        lineTo(14.6f, 13f)
        lineTo(9.7f, 17.9f)
        lineTo(8.3f, 16.5f)
        lineTo(10.8f, 14f)
        horizontalLineTo(1f)
        verticalLineTo(12f)
        moveTo(21f, 2f)
        horizontalLineTo(3f)
        curveTo(1.9f, 2f, 1f, 2.9f, 1f, 4f)
        verticalLineTo(10.1f)
        horizontalLineTo(3f)
        verticalLineTo(6f)
        horizontalLineTo(21f)
        verticalLineTo(20f)
        horizontalLineTo(3f)
        verticalLineTo(16f)
        horizontalLineTo(1f)
        verticalLineTo(20f)
        curveTo(1f, 21.1f, 1.9f, 22f, 3f, 22f)
        horizontalLineTo(21f)
        curveTo(22.1f, 22f, 23f, 21.1f, 23f, 20f)
        verticalLineTo(4f)
        curveTo(23f, 2.9f, 22.1f, 2f, 21f, 2f)
      }
    }.build()

    return _Import!!
  }

@Suppress("ObjectPropertyName")
private var _Import: ImageVector? = null
