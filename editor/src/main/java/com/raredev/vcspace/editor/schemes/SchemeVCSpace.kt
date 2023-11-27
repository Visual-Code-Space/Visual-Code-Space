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

package com.raredev.vcspace.editor.schemes

import io.github.rosemoe.sora.widget.schemes.EditorColorScheme

class SchemeVCSpace : EditorColorScheme(true) {
  override fun applyDefault() {
    super.applyDefault()
    setColor(ANNOTATION, -0x444ad7)
    setColor(FUNCTION_NAME, -0x1)
    setColor(IDENTIFIER_NAME, -0x1)
    setColor(IDENTIFIER_VAR, -0x678956)
    setColor(LITERAL, -0x9578a7)
    setColor(OPERATOR, -0x1)
    setColor(COMMENT, -0x7f7f80)
    setColor(KEYWORD, -0x3387ce)
    setColor(WHOLE_BACKGROUND, -0xd4d4d5)
    setColor(COMPLETION_WND_BACKGROUND, -0xd4d4d5)
    setColor(COMPLETION_WND_CORNER, -0x666667)
    setColor(TEXT_NORMAL, -0x1)
    setColor(LINE_NUMBER_BACKGROUND, -0xcecccb)
    setColor(LINE_NUMBER, -0x9f9c9a)
    setColor(LINE_NUMBER_CURRENT, -0x9f9c9a)
    setColor(LINE_DIVIDER, -0x9f9c9a)
    setColor(SCROLL_BAR_THUMB, -0x59595a)
    setColor(SCROLL_BAR_THUMB_PRESSED, -0xa9a9aa)
    setColor(SELECTED_TEXT_BACKGROUND, -0xc98948)
    setColor(MATCHED_TEXT_BACKGROUND, -0xcda6c3)
    setColor(CURRENT_LINE, -0xcdcdce)
    setColor(SELECTION_INSERT, -0x1)
    setColor(SELECTION_HANDLE, -0x1)
    setColor(BLOCK_LINE, -0xa8a8a9)
    setColor(BLOCK_LINE_CURRENT, -0x22a8a8a9)
    setColor(NON_PRINTABLE_CHAR, -0x222223)
    setColor(TEXT_SELECTED, -0x1)
    setColor(HIGHLIGHTED_DELIMITERS_FOREGROUND, -0x1)
  }
}
