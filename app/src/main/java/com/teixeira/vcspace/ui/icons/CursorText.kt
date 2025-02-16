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

val CursorText: ImageVector
    get() {
        if (_CursorText != null) {
            return _CursorText!!
        }
        _CursorText = ImageVector.Builder(
            name = "CursorText",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(13f, 19f)
                arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 14f, 20f)
                horizontalLineTo(16f)
                verticalLineTo(22f)
                horizontalLineTo(13.5f)
                curveTo(12.95f, 22f, 12f, 21.55f, 12f, 21f)
                curveTo(12f, 21.55f, 11.05f, 22f, 10.5f, 22f)
                horizontalLineTo(8f)
                verticalLineTo(20f)
                horizontalLineTo(10f)
                arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 11f, 19f)
                verticalLineTo(5f)
                arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 10f, 4f)
                horizontalLineTo(8f)
                verticalLineTo(2f)
                horizontalLineTo(10.5f)
                curveTo(11.05f, 2f, 12f, 2.45f, 12f, 3f)
                curveTo(12f, 2.45f, 12.95f, 2f, 13.5f, 2f)
                horizontalLineTo(16f)
                verticalLineTo(4f)
                horizontalLineTo(14f)
                arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 13f, 5f)
                verticalLineTo(19f)
                close()
            }
        }.build()

        return _CursorText!!
    }

@Suppress("ObjectPropertyName")
private var _CursorText: ImageVector? = null
