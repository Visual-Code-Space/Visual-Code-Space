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

package com.teixeira.vcspace.editor.language.java

import io.github.rosemoe.sora.lang.QuickQuoteHandler
import io.github.rosemoe.sora.lang.QuickQuoteHandler.HandleResult
import io.github.rosemoe.sora.lang.styling.Styles
import io.github.rosemoe.sora.lang.styling.StylesUtils.checkNoCompletion
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.text.TextRange

class JavaQuoteHandler : QuickQuoteHandler {
    override fun onHandleTyping(
        candidateCharacter: String,
        text: Content,
        cursor: TextRange,
        style: Styles?
    ): HandleResult {
        if (!checkNoCompletion(style, cursor.start) &&
            !checkNoCompletion(style, cursor.end) &&
            "\"" == candidateCharacter &&
            cursor.start.line == cursor.end.line
        ) {
            text.insert(cursor.start.line, cursor.start.column, "\"")
            text.insert(cursor.end.line, cursor.end.column + 1, "\"")

            return HandleResult(
                /* consumed = */ true,
                /* newCursorRange = */ TextRange(
                    text.indexer.getCharPosition(cursor.startIndex + 1),
                    text.indexer.getCharPosition(cursor.endIndex + 1)
                )
            )
        }
        return HandleResult.NOT_CONSUMED
    }
}