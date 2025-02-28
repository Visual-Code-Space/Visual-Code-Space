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

val Icons.LanguageC: ImageVector
    get() {
        if (_LanguageC != null) {
            return _LanguageC!!
        }
        _LanguageC = ImageVector.Builder(
            name = "LanguageC",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(15.45f, 15.97f)
                lineTo(15.87f, 18.41f)
                curveTo(15.61f, 18.55f, 15.19f, 18.68f, 14.63f, 18.8f)
                curveTo(14.06f, 18.93f, 13.39f, 19f, 12.62f, 19f)
                curveTo(10.41f, 18.96f, 8.75f, 18.3f, 7.64f, 17.04f)
                curveTo(6.5f, 15.77f, 5.96f, 14.16f, 5.96f, 12.21f)
                curveTo(6f, 9.9f, 6.68f, 8.13f, 8f, 6.89f)
                curveTo(9.28f, 5.64f, 10.92f, 5f, 12.9f, 5f)
                curveTo(13.65f, 5f, 14.3f, 5.07f, 14.84f, 5.19f)
                curveTo(15.38f, 5.31f, 15.78f, 5.44f, 16.04f, 5.59f)
                lineTo(15.44f, 8.08f)
                lineTo(14.4f, 7.74f)
                curveTo(14f, 7.64f, 13.53f, 7.59f, 13f, 7.59f)
                curveTo(11.85f, 7.58f, 10.89f, 7.95f, 10.14f, 8.69f)
                curveTo(9.38f, 9.42f, 9f, 10.54f, 8.96f, 12.03f)
                curveTo(8.97f, 13.39f, 9.33f, 14.45f, 10.04f, 15.23f)
                curveTo(10.75f, 16f, 11.74f, 16.4f, 13.03f, 16.41f)
                lineTo(14.36f, 16.29f)
                curveTo(14.79f, 16.21f, 15.15f, 16.1f, 15.45f, 15.97f)
                close()
            }
        }.build()

        return _LanguageC!!
    }

@Suppress("ObjectPropertyName")
private var _LanguageC: ImageVector? = null
