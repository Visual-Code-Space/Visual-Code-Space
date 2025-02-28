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

val Icons.LanguageHtml5: ImageVector
    get() {
        if (_LanguageHtml5 != null) {
            return _LanguageHtml5!!
        }
        _LanguageHtml5 = ImageVector.Builder(
            name = "LanguageHtml5",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(12f, 17.56f)
                lineTo(16.07f, 16.43f)
                lineTo(16.62f, 10.33f)
                horizontalLineTo(9.38f)
                lineTo(9.2f, 8.3f)
                horizontalLineTo(16.8f)
                lineTo(17f, 6.31f)
                horizontalLineTo(7f)
                lineTo(7.56f, 12.32f)
                horizontalLineTo(14.45f)
                lineTo(14.22f, 14.9f)
                lineTo(12f, 15.5f)
                lineTo(9.78f, 14.9f)
                lineTo(9.64f, 13.24f)
                horizontalLineTo(7.64f)
                lineTo(7.93f, 16.43f)
                lineTo(12f, 17.56f)
                moveTo(4.07f, 3f)
                horizontalLineTo(19.93f)
                lineTo(18.5f, 19.2f)
                lineTo(12f, 21f)
                lineTo(5.5f, 19.2f)
                lineTo(4.07f, 3f)
                close()
            }
        }.build()

        return _LanguageHtml5!!
    }

@Suppress("ObjectPropertyName")
private var _LanguageHtml5: ImageVector? = null
