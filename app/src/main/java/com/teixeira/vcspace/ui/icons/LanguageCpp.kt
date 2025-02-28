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

package com.teixeira.vcspace.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.teixeira.vcspace.ui.icons.Icons

val Icons.LanguageCpp: ImageVector
    get() {
        if (_LanguageCpp != null) {
            return _LanguageCpp!!
        }
        _LanguageCpp = ImageVector.Builder(
            name = "LanguageCpp",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(10.5f, 15.97f)
                lineTo(10.91f, 18.41f)
                curveTo(10.65f, 18.55f, 10.23f, 18.68f, 9.67f, 18.8f)
                curveTo(9.1f, 18.93f, 8.43f, 19f, 7.66f, 19f)
                curveTo(5.45f, 18.96f, 3.79f, 18.3f, 2.68f, 17.04f)
                curveTo(1.56f, 15.77f, 1f, 14.16f, 1f, 12.21f)
                curveTo(1.05f, 9.9f, 1.72f, 8.13f, 3f, 6.89f)
                curveTo(4.32f, 5.64f, 5.96f, 5f, 7.94f, 5f)
                curveTo(8.69f, 5f, 9.34f, 5.07f, 9.88f, 5.19f)
                curveTo(10.42f, 5.31f, 10.82f, 5.44f, 11.08f, 5.59f)
                lineTo(10.5f, 8.08f)
                lineTo(9.44f, 7.74f)
                curveTo(9.04f, 7.64f, 8.58f, 7.59f, 8.05f, 7.59f)
                curveTo(6.89f, 7.58f, 5.93f, 7.95f, 5.18f, 8.69f)
                curveTo(4.42f, 9.42f, 4.03f, 10.54f, 4f, 12.03f)
                curveTo(4f, 13.39f, 4.37f, 14.45f, 5.08f, 15.23f)
                curveTo(5.79f, 16f, 6.79f, 16.4f, 8.07f, 16.41f)
                lineTo(9.4f, 16.29f)
                curveTo(9.83f, 16.21f, 10.19f, 16.1f, 10.5f, 15.97f)
                moveTo(11f, 11f)
                horizontalLineTo(13f)
                verticalLineTo(9f)
                horizontalLineTo(15f)
                verticalLineTo(11f)
                horizontalLineTo(17f)
                verticalLineTo(13f)
                horizontalLineTo(15f)
                verticalLineTo(15f)
                horizontalLineTo(13f)
                verticalLineTo(13f)
                horizontalLineTo(11f)
                verticalLineTo(11f)
                moveTo(18f, 11f)
                horizontalLineTo(20f)
                verticalLineTo(9f)
                horizontalLineTo(22f)
                verticalLineTo(11f)
                horizontalLineTo(24f)
                verticalLineTo(13f)
                horizontalLineTo(22f)
                verticalLineTo(15f)
                horizontalLineTo(20f)
                verticalLineTo(13f)
                horizontalLineTo(18f)
                verticalLineTo(11f)
                close()
            }
        }.build()

        return _LanguageCpp!!
    }

@Suppress("ObjectPropertyName")
private var _LanguageCpp: ImageVector? = null
