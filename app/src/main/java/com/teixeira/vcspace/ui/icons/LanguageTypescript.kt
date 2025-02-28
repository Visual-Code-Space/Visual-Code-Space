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

val Icons.LanguageTypescript: ImageVector
    get() {
        if (_LanguageTypescript != null) {
            return _LanguageTypescript!!
        }
        _LanguageTypescript = ImageVector.Builder(
            name = "LanguageTypescript",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(3f, 3f)
                horizontalLineTo(21f)
                verticalLineTo(21f)
                horizontalLineTo(3f)
                verticalLineTo(3f)
                moveTo(13.71f, 17.86f)
                curveTo(14.21f, 18.84f, 15.22f, 19.59f, 16.8f, 19.59f)
                curveTo(18.4f, 19.59f, 19.6f, 18.76f, 19.6f, 17.23f)
                curveTo(19.6f, 15.82f, 18.79f, 15.19f, 17.35f, 14.57f)
                lineTo(16.93f, 14.39f)
                curveTo(16.2f, 14.08f, 15.89f, 13.87f, 15.89f, 13.37f)
                curveTo(15.89f, 12.96f, 16.2f, 12.64f, 16.7f, 12.64f)
                curveTo(17.18f, 12.64f, 17.5f, 12.85f, 17.79f, 13.37f)
                lineTo(19.1f, 12.5f)
                curveTo(18.55f, 11.54f, 17.77f, 11.17f, 16.7f, 11.17f)
                curveTo(15.19f, 11.17f, 14.22f, 12.13f, 14.22f, 13.4f)
                curveTo(14.22f, 14.78f, 15.03f, 15.43f, 16.25f, 15.95f)
                lineTo(16.67f, 16.13f)
                curveTo(17.45f, 16.47f, 17.91f, 16.68f, 17.91f, 17.26f)
                curveTo(17.91f, 17.74f, 17.46f, 18.09f, 16.76f, 18.09f)
                curveTo(15.93f, 18.09f, 15.45f, 17.66f, 15.09f, 17.06f)
                lineTo(13.71f, 17.86f)
                moveTo(13f, 11.25f)
                horizontalLineTo(8f)
                verticalLineTo(12.75f)
                horizontalLineTo(9.5f)
                verticalLineTo(20f)
                horizontalLineTo(11.25f)
                verticalLineTo(12.75f)
                horizontalLineTo(13f)
                verticalLineTo(11.25f)
                close()
            }
        }.build()

        return _LanguageTypescript!!
    }

@Suppress("ObjectPropertyName")
private var _LanguageTypescript: ImageVector? = null
