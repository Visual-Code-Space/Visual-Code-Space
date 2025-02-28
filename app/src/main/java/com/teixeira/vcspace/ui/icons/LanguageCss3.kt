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

val Icons.LanguageCss3: ImageVector
    get() {
        if (_LanguageCss3 != null) {
            return _LanguageCss3!!
        }
        _LanguageCss3 = ImageVector.Builder(
            name = "LanguageCss3",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(5f, 3f)
                lineTo(4.35f, 6.34f)
                horizontalLineTo(17.94f)
                lineTo(17.5f, 8.5f)
                horizontalLineTo(3.92f)
                lineTo(3.26f, 11.83f)
                horizontalLineTo(16.85f)
                lineTo(16.09f, 15.64f)
                lineTo(10.61f, 17.45f)
                lineTo(5.86f, 15.64f)
                lineTo(6.19f, 14f)
                horizontalLineTo(2.85f)
                lineTo(2.06f, 18f)
                lineTo(9.91f, 21f)
                lineTo(18.96f, 18f)
                lineTo(20.16f, 11.97f)
                lineTo(20.4f, 10.76f)
                lineTo(21.94f, 3f)
                horizontalLineTo(5f)
                close()
            }
        }.build()

        return _LanguageCss3!!
    }

@Suppress("ObjectPropertyName")
private var _LanguageCss3: ImageVector? = null
