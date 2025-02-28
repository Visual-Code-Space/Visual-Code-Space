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

val Icons.LanguagePhp: ImageVector
    get() {
        if (_LanguagePhp != null) {
            return _LanguagePhp!!
        }
        _LanguagePhp = ImageVector.Builder(
            name = "LanguagePhp",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(12f, 18.08f)
                curveTo(5.37f, 18.08f, 0f, 15.36f, 0f, 12f)
                curveTo(0f, 8.64f, 5.37f, 5.92f, 12f, 5.92f)
                curveTo(18.63f, 5.92f, 24f, 8.64f, 24f, 12f)
                curveTo(24f, 15.36f, 18.63f, 18.08f, 12f, 18.08f)
                moveTo(6.81f, 10.13f)
                curveTo(7.35f, 10.13f, 7.72f, 10.23f, 7.9f, 10.44f)
                curveTo(8.08f, 10.64f, 8.12f, 11f, 8.03f, 11.47f)
                curveTo(7.93f, 12f, 7.74f, 12.34f, 7.45f, 12.56f)
                curveTo(7.17f, 12.78f, 6.74f, 12.89f, 6.16f, 12.89f)
                horizontalLineTo(5.29f)
                lineTo(5.82f, 10.13f)
                horizontalLineTo(6.81f)
                moveTo(3.31f, 15.68f)
                horizontalLineTo(4.75f)
                lineTo(5.09f, 13.93f)
                horizontalLineTo(6.32f)
                curveTo(6.86f, 13.93f, 7.3f, 13.87f, 7.65f, 13.76f)
                curveTo(8f, 13.64f, 8.32f, 13.45f, 8.61f, 13.18f)
                curveTo(8.85f, 12.96f, 9.04f, 12.72f, 9.19f, 12.45f)
                curveTo(9.34f, 12.19f, 9.45f, 11.89f, 9.5f, 11.57f)
                curveTo(9.66f, 10.79f, 9.55f, 10.18f, 9.17f, 9.75f)
                curveTo(8.78f, 9.31f, 8.18f, 9.1f, 7.35f, 9.1f)
                horizontalLineTo(4.59f)
                lineTo(3.31f, 15.68f)
                moveTo(10.56f, 7.35f)
                lineTo(9.28f, 13.93f)
                horizontalLineTo(10.7f)
                lineTo(11.44f, 10.16f)
                horizontalLineTo(12.58f)
                curveTo(12.94f, 10.16f, 13.18f, 10.22f, 13.29f, 10.34f)
                curveTo(13.4f, 10.46f, 13.42f, 10.68f, 13.36f, 11f)
                lineTo(12.79f, 13.93f)
                horizontalLineTo(14.24f)
                lineTo(14.83f, 10.86f)
                curveTo(14.96f, 10.24f, 14.86f, 9.79f, 14.56f, 9.5f)
                curveTo(14.26f, 9.23f, 13.71f, 9.1f, 12.91f, 9.1f)
                horizontalLineTo(11.64f)
                lineTo(12f, 7.35f)
                horizontalLineTo(10.56f)
                moveTo(18f, 10.13f)
                curveTo(18.55f, 10.13f, 18.91f, 10.23f, 19.09f, 10.44f)
                curveTo(19.27f, 10.64f, 19.31f, 11f, 19.22f, 11.47f)
                curveTo(19.12f, 12f, 18.93f, 12.34f, 18.65f, 12.56f)
                curveTo(18.36f, 12.78f, 17.93f, 12.89f, 17.35f, 12.89f)
                horizontalLineTo(16.5f)
                lineTo(17f, 10.13f)
                horizontalLineTo(18f)
                moveTo(14.5f, 15.68f)
                horizontalLineTo(15.94f)
                lineTo(16.28f, 13.93f)
                horizontalLineTo(17.5f)
                curveTo(18.05f, 13.93f, 18.5f, 13.87f, 18.85f, 13.76f)
                curveTo(19.2f, 13.64f, 19.5f, 13.45f, 19.8f, 13.18f)
                curveTo(20.04f, 12.96f, 20.24f, 12.72f, 20.38f, 12.45f)
                curveTo(20.53f, 12.19f, 20.64f, 11.89f, 20.7f, 11.57f)
                curveTo(20.85f, 10.79f, 20.74f, 10.18f, 20.36f, 9.75f)
                curveTo(20f, 9.31f, 19.37f, 9.1f, 18.54f, 9.1f)
                horizontalLineTo(15.79f)
                lineTo(14.5f, 15.68f)
                close()
            }
        }.build()

        return _LanguagePhp!!
    }

@Suppress("ObjectPropertyName")
private var _LanguagePhp: ImageVector? = null
