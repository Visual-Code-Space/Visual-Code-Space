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

val Icons.LanguageXml: ImageVector
    get() {
        if (_LanguageXml != null) {
            return _LanguageXml!!
        }
        _LanguageXml = ImageVector.Builder(
            name = "LanguageXml",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(12.89f, 3f)
                lineTo(14.85f, 3.4f)
                lineTo(11.11f, 21f)
                lineTo(9.15f, 20.6f)
                lineTo(12.89f, 3f)
                moveTo(19.59f, 12f)
                lineTo(16f, 8.41f)
                verticalLineTo(5.58f)
                lineTo(22.42f, 12f)
                lineTo(16f, 18.41f)
                verticalLineTo(15.58f)
                lineTo(19.59f, 12f)
                moveTo(1.58f, 12f)
                lineTo(8f, 5.58f)
                verticalLineTo(8.41f)
                lineTo(4.41f, 12f)
                lineTo(8f, 15.58f)
                verticalLineTo(18.41f)
                lineTo(1.58f, 12f)
                close()
            }
        }.build()

        return _LanguageXml!!
    }

@Suppress("ObjectPropertyName")
private var _LanguageXml: ImageVector? = null
