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

package com.itsvks.monaco.option

@JvmInline
value class TextEditorCursorStyle private constructor(val value: Int) {
  companion object {
    /**
     * As a vertical line (sitting between two characters).
     */
    val Line = TextEditorCursorStyle(1)

    /**
     * As a block (sitting on top of a character).
     */
    val Block = TextEditorCursorStyle(2)

    /**
     * As a horizontal line (sitting under a character).
     */
    val Underline = TextEditorCursorStyle(3)

    /**
     * As a thin vertical line (sitting between two characters).
     */
    val LineThin = TextEditorCursorStyle(4)

    /**
     * As an outlined block (sitting on top of a character).
     */
    val BlockOutline = TextEditorCursorStyle(5)

    /**
     * As a thin horizontal line (sitting under a character).
     */
    val UnderlineThin = TextEditorCursorStyle(6)
  }
}
