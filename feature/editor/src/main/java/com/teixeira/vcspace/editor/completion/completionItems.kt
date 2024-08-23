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

package com.teixeira.vcspace.editor.completion

import io.github.rosemoe.sora.lang.completion.CompletionItem
import io.github.rosemoe.sora.lang.completion.SnippetDescription
import io.github.rosemoe.sora.text.CharPosition
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.widget.CodeEditor

abstract class VCSpaceCompletionItem(
  val completionKind: CompletionItemKind,
  label: CharSequence,
  desc: CharSequence,
) : CompletionItem(label, desc, null)

class SimpleCompletionItem(
  completionKind: CompletionItemKind,
  label: CharSequence,
  desc: CharSequence,
  val prefixLength: Int,
  private val commitText: String,
) : VCSpaceCompletionItem(completionKind, label, desc) {

  override fun performCompletion(editor: CodeEditor, text: Content, line: Int, column: Int) {
    if (prefixLength == 0) {
      text.insert(line, column, commitText)
      return
    }
    text.replace(line, column - prefixLength, line, column, commitText)
  }
}

class SimpleSnippetCompletionItem(
  label: CharSequence,
  desc: CharSequence,
  val snippet: SnippetDescription,
) : VCSpaceCompletionItem(CompletionItemKind.SNIPPET, label, desc) {

  override fun performCompletion(editor: CodeEditor, text: Content, position: CharPosition) {
    val prefixLength = snippet.selectedLength

    val selectedText = text.subSequence(position.index - prefixLength, position.index).toString()

    var actionIndex = position.index
    if (snippet.deleteSelected) {
      text.delete(position.index - prefixLength, position.index)
      actionIndex -= prefixLength
    }
    editor.snippetController.startSnippet(actionIndex, snippet.snippet, selectedText)
  }

  override fun performCompletion(editor: CodeEditor, text: Content, line: Int, column: Int) {
    // do nothing
  }
}

enum class CompletionItemKind {
  VALUE,
  KEYWORD,
  SNIPPET,
  FILE,
  FOLDER,
  TAG,
  ATTRIBUTE,
  IDENTIFIER,
  UNKNOWN,
}
