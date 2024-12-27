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

import android.os.Bundle
import androidx.compose.ui.util.fastForEach
import com.teixeira.vcspace.editor.completion.SimpleSnippetCompletionItem
import com.teixeira.vcspace.editor.snippet.SnippetController
import io.github.rosemoe.sora.lang.EmptyLanguage
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.lang.QuickQuoteHandler
import io.github.rosemoe.sora.lang.analysis.AnalyzeManager
import io.github.rosemoe.sora.lang.completion.CompletionHelper
import io.github.rosemoe.sora.lang.completion.CompletionPublisher
import io.github.rosemoe.sora.lang.completion.IdentifierAutoComplete
import io.github.rosemoe.sora.lang.completion.SnippetDescription
import io.github.rosemoe.sora.lang.completion.snippet.CodeSnippet
import io.github.rosemoe.sora.lang.completion.snippet.parser.CodeSnippetParser
import io.github.rosemoe.sora.lang.format.Formatter
import io.github.rosemoe.sora.lang.smartEnter.NewlineHandleResult
import io.github.rosemoe.sora.lang.smartEnter.NewlineHandler
import io.github.rosemoe.sora.lang.styling.Styles
import io.github.rosemoe.sora.lang.styling.StylesUtils
import io.github.rosemoe.sora.text.CharPosition
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.text.ContentReference
import io.github.rosemoe.sora.text.TextUtils
import io.github.rosemoe.sora.util.MyCharacter
import io.github.rosemoe.sora.widget.SymbolPairMatch
import io.github.rosemoe.sora.widget.SymbolPairMatch.DefaultSymbolPairs
import kotlin.math.max
import kotlin.math.min

/**
 * Java language.
 * Simple implementation.
 *
 * @author Rosemoe, Vivek
 */
class JavaLanguage : Language {
  private var autoComplete: IdentifierAutoComplete?
  private val manager: JavaIncrementalAnalyzeManager
  private val javaQuoteHandler = JavaQuoteHandler()

  private val snippetController = SnippetController.instance

  override fun getAnalyzeManager(): AnalyzeManager {
    return manager
  }

  override fun getQuickQuoteHandler(): QuickQuoteHandler {
    return javaQuoteHandler
  }

  override fun destroy() {
    autoComplete = null
  }

  override fun getInterruptionLevel(): Int {
    return Language.INTERRUPTION_LEVEL_STRONG
  }

  override fun requireAutoComplete(
    content: ContentReference,
    position: CharPosition,
    publisher: CompletionPublisher,
    extraArguments: Bundle
  ) {
    val prefix = CompletionHelper.computePrefix(content, position, MyCharacter::isJavaIdentifierPart)

    snippetController.javaSnippets.fastForEach { snippet ->
      if (snippet.trigger.startsWith(prefix) && prefix.isNotEmpty()) {
        publisher.addItem(
          SimpleSnippetCompletionItem(
            snippet.trigger,
            snippet.description,
            SnippetDescription(prefix.length, CodeSnippetParser.parse(snippet.snippet), true)
          )
        )
      }
    }

    val idt = manager.identifiers
    autoComplete!!.requireAutoComplete(content, position, prefix, publisher, idt)
  }

  override fun getIndentAdvance(text: ContentReference, line: Int, column: Int): Int {
    val content = text.getLine(line).substring(0, column)
    return getIndentAdvance(content)
  }

  private fun getIndentAdvance(content: String): Int {
    val t = JavaTextTokenizer(content)
    var token: Tokens
    var advance = 0
    while ((t.nextToken().also { token = it }) != Tokens.EOF) {
      if (token == Tokens.LBRACE) {
        advance++
      }
    }
    advance = max(0.0, advance.toDouble()).toInt()
    return advance * 4
  }

  private val newlineHandlers = arrayOf<NewlineHandler>(BraceHandler())

  init {
    autoComplete = IdentifierAutoComplete(JavaTextTokenizer.sKeywords.toTypedArray())
    manager = JavaIncrementalAnalyzeManager()
  }

  override fun useTab(): Boolean {
    return false
  }

  override fun getFormatter(): Formatter {
    return EmptyLanguage.EmptyFormatter.INSTANCE
  }

  override fun getSymbolPairs(): SymbolPairMatch {
    return DefaultSymbolPairs()
  }

  override fun getNewlineHandlers(): Array<NewlineHandler> {
    return newlineHandlers
  }

  internal inner class BraceHandler : NewlineHandler {
    override fun matchesRequirement(
      text: Content,
      position: CharPosition,
      style: Styles?
    ): Boolean {
      val line = text.getLine(position.line)
      return !StylesUtils.checkNoCompletion(style, position) && getNonEmptyTextBefore(
        line,
        position.column,
        1
      ) == "{" && getNonEmptyTextAfter(line, position.column, 1) == "}"
    }

    override fun handleNewline(
      text: Content,
      position: CharPosition,
      style: Styles?,
      tabSize: Int
    ): NewlineHandleResult {
      val line = text.getLine(position.line)
      val index = position.column
      val beforeText = line.subSequence(0, index).toString()
      val afterText = line.subSequence(index, line.length).toString()
      return handleNewline(beforeText, afterText, tabSize)
    }

    fun handleNewline(beforeText: String, afterText: String, tabSize: Int): NewlineHandleResult {
      val count = TextUtils.countLeadingSpaceCount(beforeText, tabSize)
      val advanceBefore = getIndentAdvance(beforeText)
      val advanceAfter = getIndentAdvance(afterText)
      var text: String
      val sb = StringBuilder("\n")
        .append(TextUtils.createIndent(count + advanceBefore, tabSize, useTab()))
        .append('\n')
        .append(TextUtils.createIndent(count + advanceAfter, tabSize, useTab()).also { text = it })
      val shiftLeft = text.length + 1
      return NewlineHandleResult(sb, shiftLeft)
    }
  }

  companion object {
    private val FOR_SNIPPET: CodeSnippet =
      CodeSnippetParser.parse("for(int \${1:i} = 0;$1 < \${2:count};$1++) {\n    $0\n}")
    private val STATIC_CONST_SNIPPET: CodeSnippet =
      CodeSnippetParser.parse("private final static \${1:type} \${2/(.*)/\${1:/upcase}/} = \${3:value};")
    private val CLIPBOARD_SNIPPET: CodeSnippet = CodeSnippetParser.parse("\${1:\${CLIPBOARD}}")

    private fun getNonEmptyTextBefore(text: CharSequence, index: Int, length: Int): String {
      var index = index
      while (index > 0 && Character.isWhitespace(text[index - 1])) {
        index--
      }
      return text.subSequence(max(0.0, (index - length).toDouble()).toInt(), index).toString()
    }

    private fun getNonEmptyTextAfter(text: CharSequence, index: Int, length: Int): String {
      var index = index
      while (index < text.length && Character.isWhitespace(text[index])) {
        index++
      }
      return text.subSequence(
        index,
        min((index + length).toDouble(), text.length.toDouble()).toInt()
      ).toString()
    }
  }
}