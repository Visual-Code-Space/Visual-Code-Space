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

val Icons.LanguageCsharp: ImageVector
    get() {
        if (_LanguageCsharp != null) {
            return _LanguageCsharp!!
        }
        _LanguageCsharp = ImageVector.Builder(
            name = "LanguageCsharp",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(11.5f, 15.97f)
                lineTo(11.91f, 18.41f)
                curveTo(11.65f, 18.55f, 11.23f, 18.68f, 10.67f, 18.8f)
                curveTo(10.1f, 18.93f, 9.43f, 19f, 8.66f, 19f)
                curveTo(6.45f, 18.96f, 4.79f, 18.3f, 3.68f, 17.04f)
                curveTo(2.56f, 15.77f, 2f, 14.16f, 2f, 12.21f)
                curveTo(2.05f, 9.9f, 2.72f, 8.13f, 4f, 6.89f)
                curveTo(5.32f, 5.64f, 6.96f, 5f, 8.94f, 5f)
                curveTo(9.69f, 5f, 10.34f, 5.07f, 10.88f, 5.19f)
                curveTo(11.42f, 5.31f, 11.82f, 5.44f, 12.08f, 5.59f)
                lineTo(11.5f, 8.08f)
                lineTo(10.44f, 7.74f)
                curveTo(10.04f, 7.64f, 9.58f, 7.59f, 9.05f, 7.59f)
                curveTo(7.89f, 7.58f, 6.93f, 7.95f, 6.18f, 8.69f)
                curveTo(5.42f, 9.42f, 5.03f, 10.54f, 5f, 12.03f)
                curveTo(5f, 13.39f, 5.37f, 14.45f, 6.08f, 15.23f)
                curveTo(6.79f, 16f, 7.79f, 16.4f, 9.07f, 16.41f)
                lineTo(10.4f, 16.29f)
                curveTo(10.83f, 16.21f, 11.19f, 16.1f, 11.5f, 15.97f)
                moveTo(13.89f, 19f)
                lineTo(14.5f, 15f)
                horizontalLineTo(13f)
                lineTo(13.34f, 13f)
                horizontalLineTo(14.84f)
                lineTo(15.16f, 11f)
                horizontalLineTo(13.66f)
                lineTo(14f, 9f)
                horizontalLineTo(15.5f)
                lineTo(16.11f, 5f)
                horizontalLineTo(18.11f)
                lineTo(17.5f, 9f)
                horizontalLineTo(18.5f)
                lineTo(19.11f, 5f)
                horizontalLineTo(21.11f)
                lineTo(20.5f, 9f)
                horizontalLineTo(22f)
                lineTo(21.66f, 11f)
                horizontalLineTo(20.16f)
                lineTo(19.84f, 13f)
                horizontalLineTo(21.34f)
                lineTo(21f, 15f)
                horizontalLineTo(19.5f)
                lineTo(18.89f, 19f)
                horizontalLineTo(16.89f)
                lineTo(17.5f, 15f)
                horizontalLineTo(16.5f)
                lineTo(15.89f, 19f)
                horizontalLineTo(13.89f)
                moveTo(16.84f, 13f)
                horizontalLineTo(17.84f)
                lineTo(18.16f, 11f)
                horizontalLineTo(17.16f)
                lineTo(16.84f, 13f)
                close()
            }
        }.build()

        return _LanguageCsharp!!
    }

@Suppress("ObjectPropertyName")
private var _LanguageCsharp: ImageVector? = null
