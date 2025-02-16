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

val FormatTextWrap: ImageVector
    get() {
        if (_FormatTextWrap != null) {
            return _FormatTextWrap!!
        }
        _FormatTextWrap = ImageVector.Builder(
            name = "FormatTextWrap",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(160f, 800f)
                lineTo(160f, 160f)
                lineTo(240f, 160f)
                lineTo(240f, 800f)
                lineTo(160f, 800f)
                close()
                moveTo(720f, 800f)
                lineTo(720f, 160f)
                lineTo(800f, 160f)
                lineTo(800f, 800f)
                lineTo(720f, 800f)
                close()
                moveTo(424f, 702f)
                lineTo(282f, 560f)
                lineTo(424f, 419f)
                lineTo(480f, 475f)
                lineTo(435f, 520f)
                lineTo(520f, 520f)
                quadTo(553f, 520f, 576.5f, 496.5f)
                quadTo(600f, 473f, 600f, 440f)
                quadTo(600f, 407f, 576.5f, 383.5f)
                quadTo(553f, 360f, 520f, 360f)
                lineTo(280f, 360f)
                lineTo(280f, 280f)
                lineTo(520f, 280f)
                quadTo(586f, 280f, 633f, 327f)
                quadTo(680f, 374f, 680f, 440f)
                quadTo(680f, 506f, 633f, 553f)
                quadTo(586f, 600f, 520f, 600f)
                lineTo(435f, 600f)
                lineTo(480f, 645f)
                lineTo(424f, 702f)
                close()
            }
        }.build()

        return _FormatTextWrap!!
    }

@Suppress("ObjectPropertyName")
private var _FormatTextWrap: ImageVector? = null
