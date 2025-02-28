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

val Icons.LanguageJava: ImageVector
    get() {
        if (_LanguageJava != null) {
            return _LanguageJava!!
        }
        _LanguageJava = ImageVector.Builder(
            name = "LanguageJava",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(16.5f, 6.08f)
                curveTo(16.5f, 6.08f, 9.66f, 7.79f, 12.94f, 11.56f)
                curveTo(13.91f, 12.67f, 12.69f, 13.67f, 12.69f, 13.67f)
                curveTo(12.69f, 13.67f, 15.14f, 12.42f, 14f, 10.82f)
                curveTo(12.94f, 9.35f, 12.14f, 8.62f, 16.5f, 6.08f)
                moveTo(12.03f, 7.28f)
                curveTo(16.08f, 4.08f, 14f, 2f, 14f, 2f)
                curveTo(14.84f, 5.3f, 11.04f, 6.3f, 9.67f, 8.36f)
                curveTo(8.73f, 9.76f, 10.13f, 11.27f, 12f, 13f)
                curveTo(11.29f, 11.3f, 8.78f, 9.84f, 12.03f, 7.28f)
                moveTo(9.37f, 17.47f)
                curveTo(6.29f, 18.33f, 11.25f, 20.1f, 15.16f, 18.43f)
                curveTo(14.78f, 18.28f, 14.41f, 18.1f, 14.06f, 17.89f)
                curveTo(12.7f, 18.2f, 11.3f, 18.26f, 9.92f, 18.07f)
                curveTo(8.61f, 17.91f, 9.37f, 17.47f, 9.37f, 17.47f)
                moveTo(14.69f, 15.79f)
                curveTo(12.94f, 16.17f, 11.13f, 16.26f, 9.35f, 16.05f)
                curveTo(8.04f, 15.92f, 8.9f, 15.28f, 8.9f, 15.28f)
                curveTo(5.5f, 16.41f, 10.78f, 17.68f, 15.5f, 16.3f)
                curveTo(15.21f, 16.19f, 14.93f, 16f, 14.69f, 15.79f)
                moveTo(18.11f, 19.09f)
                curveTo(18.11f, 19.09f, 18.68f, 19.56f, 17.5f, 19.92f)
                curveTo(15.22f, 20.6f, 8.07f, 20.81f, 6.09f, 19.95f)
                curveTo(5.38f, 19.64f, 6.72f, 19.21f, 7.14f, 19.12f)
                curveTo(7.37f, 19.06f, 7.6f, 19.04f, 7.83f, 19.04f)
                curveTo(7.04f, 18.5f, 2.7f, 20.14f, 5.64f, 20.6f)
                curveTo(13.61f, 21.9f, 20.18f, 20f, 18.11f, 19.09f)
                moveTo(15.37f, 14.23f)
                curveTo(15.66f, 14.04f, 15.97f, 13.88f, 16.29f, 13.74f)
                curveTo(16.29f, 13.74f, 14.78f, 14f, 13.27f, 14.14f)
                curveTo(11.67f, 14.3f, 10.06f, 14.32f, 8.46f, 14.2f)
                curveTo(6.11f, 13.89f, 9.75f, 13f, 9.75f, 13f)
                curveTo(8.65f, 13f, 7.57f, 13.26f, 6.59f, 13.75f)
                curveTo(4.54f, 14.75f, 11.69f, 15.2f, 15.37f, 14.23f)
                moveTo(16.27f, 16.65f)
                curveTo(16.25f, 16.69f, 16.23f, 16.72f, 16.19f, 16.75f)
                curveTo(21.2f, 15.44f, 19.36f, 12.11f, 16.96f, 12.94f)
                curveTo(16.83f, 13f, 16.72f, 13.08f, 16.65f, 13.19f)
                curveTo(16.79f, 13.14f, 16.93f, 13.1f, 17.08f, 13.07f)
                curveTo(18.28f, 12.83f, 20f, 14.7f, 16.27f, 16.65f)
                moveTo(16.4f, 21.26f)
                curveTo(13.39f, 21.78f, 10.31f, 21.82f, 7.28f, 21.4f)
                curveTo(7.28f, 21.4f, 7.74f, 21.78f, 10.09f, 21.93f)
                curveTo(13.69f, 22.16f, 19.22f, 21.8f, 19.35f, 20.1f)
                curveTo(19.38f, 20.11f, 19.12f, 20.75f, 16.4f, 21.26f)
                close()
            }
        }.build()

        return _LanguageJava!!
    }

@Suppress("ObjectPropertyName")
private var _LanguageJava: ImageVector? = null
