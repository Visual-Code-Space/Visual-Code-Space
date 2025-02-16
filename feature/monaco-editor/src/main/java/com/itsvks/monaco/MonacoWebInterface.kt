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

package com.itsvks.monaco

import android.webkit.JavascriptInterface

class MonacoWebInterface(private val editor: MonacoEditor) {
    @set:JavascriptInterface
    var value = ""

    @set:JavascriptInterface
    var canUndo = false

    @set:JavascriptInterface
    var canRedo = false

    @set:JavascriptInterface
    var isModified = false

    @set:JavascriptInterface
    var lineNumber: Int = 1

    @set:JavascriptInterface
    var column: Int = 1

    @JavascriptInterface
    fun onTextChanged(content: String) {
        editor.onContentChange(content)
    }

    @JavascriptInterface
    fun onInlineCompletion(
        language: String,
        textBeforeCursor: String,
        textAfterCursor: String
    ): String? {
        return editor.inlineCompletionProvider?.provide(language, textBeforeCursor, textAfterCursor)
    }
}
