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

val PromptSuggestion: ImageVector
    get() {
        if (_PromptSuggestion != null) {
            return _PromptSuggestion!!
        }
        _PromptSuggestion = ImageVector.Builder(
            name = "PromptSuggestion",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
            autoMirror = true
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(600f, 760f)
                lineTo(544f, 703f)
                lineTo(687f, 560f)
                lineTo(300f, 560f)
                quadTo(225f, 560f, 172.5f, 507.5f)
                quadTo(120f, 455f, 120f, 380f)
                quadTo(120f, 305f, 172.5f, 252.5f)
                quadTo(225f, 200f, 300f, 200f)
                lineTo(320f, 200f)
                lineTo(320f, 280f)
                lineTo(300f, 280f)
                quadTo(258f, 280f, 229f, 309f)
                quadTo(200f, 338f, 200f, 380f)
                quadTo(200f, 422f, 229f, 451f)
                quadTo(258f, 480f, 300f, 480f)
                lineTo(687f, 480f)
                lineTo(544f, 336f)
                lineTo(600f, 280f)
                lineTo(840f, 520f)
                lineTo(600f, 760f)
                close()
            }
        }.build()

        return _PromptSuggestion!!
    }

@Suppress("ObjectPropertyName")
private var _PromptSuggestion: ImageVector? = null
