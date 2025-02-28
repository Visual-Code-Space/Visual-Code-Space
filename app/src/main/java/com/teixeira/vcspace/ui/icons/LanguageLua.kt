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

val Icons.LanguageLua: ImageVector
    get() {
        if (_LanguageLua != null) {
            return _LanguageLua!!
        }
        _LanguageLua = ImageVector.Builder(
            name = "LanguageLua",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(10.5f, 5f)
                arcTo(8.5f, 8.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 2f, 13.5f)
                arcTo(8.5f, 8.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 10.5f, 22f)
                arcTo(8.5f, 8.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 19f, 13.5f)
                arcTo(8.5f, 8.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 10.5f, 5f)
                moveTo(13.5f, 13f)
                arcTo(2.5f, 2.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 11f, 10.5f)
                arcTo(2.5f, 2.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 13.5f, 8f)
                arcTo(2.5f, 2.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 16f, 10.5f)
                arcTo(2.5f, 2.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 13.5f, 13f)
                moveTo(19.5f, 2f)
                arcTo(2.5f, 2.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 17f, 4.5f)
                arcTo(2.5f, 2.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 19.5f, 7f)
                arcTo(2.5f, 2.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 22f, 4.5f)
                arcTo(2.5f, 2.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 19.5f, 2f)
            }
        }.build()

        return _LanguageLua!!
    }

@Suppress("ObjectPropertyName")
private var _LanguageLua: ImageVector? = null
