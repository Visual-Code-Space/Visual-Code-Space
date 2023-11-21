package com.raredev.vcspace.editor.completion

import io.github.rosemoe.sora.lang.completion.CompletionItem
import io.github.rosemoe.sora.lang.completion.SnippetDescription
import io.github.rosemoe.sora.text.CharPosition
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.widget.CodeEditor

abstract class VCSpaceCompletionItem(
    val completionKind: CompletionItemKind,
    label: CharSequence,
    desc: CharSequence
) : CompletionItem(label, desc, null)

class SimpleCompletionItem(
    completionKind: CompletionItemKind,
    label: CharSequence,
    desc: CharSequence,
    val prefixLength: Int,
    val commitText: String
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
    val snippet: SnippetDescription
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
  UNKNOWN
}
