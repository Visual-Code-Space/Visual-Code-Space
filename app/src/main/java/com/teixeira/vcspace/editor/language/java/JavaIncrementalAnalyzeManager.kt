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
import io.github.rosemoe.sora.lang.analysis.AsyncIncrementalAnalyzeManager
import io.github.rosemoe.sora.lang.analysis.IncrementalAnalyzeManager.LineTokenizeResult
import io.github.rosemoe.sora.lang.analysis.StyleReceiver
import io.github.rosemoe.sora.lang.brackets.SimpleBracketsCollector
import io.github.rosemoe.sora.lang.completion.IdentifierAutoComplete.SyncIdentifiers
import io.github.rosemoe.sora.lang.styling.CodeBlock
import io.github.rosemoe.sora.lang.styling.Span
import io.github.rosemoe.sora.lang.styling.SpanFactory
import io.github.rosemoe.sora.lang.styling.TextStyle
import io.github.rosemoe.sora.lang.styling.color.EditorColor
import io.github.rosemoe.sora.lang.styling.span.SpanClickableUrl
import io.github.rosemoe.sora.lang.styling.span.SpanExtAttrs
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.text.ContentReference
import io.github.rosemoe.sora.util.ArrayList
import io.github.rosemoe.sora.util.IntPair
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme
import java.util.Stack
import java.util.regex.Pattern


class JavaIncrementalAnalyzeManager :
  AsyncIncrementalAnalyzeManager<State, JavaIncrementalAnalyzeManager.HighlightToken?>() {
  private val tokenizerProvider = ThreadLocal<JavaTextTokenizer>()
  var identifiers: SyncIdentifiers = SyncIdentifiers()

  @Synchronized
  private fun obtainTokenizer(): JavaTextTokenizer {
    var res = tokenizerProvider.get()
    if (res == null) {
      res = JavaTextTokenizer("")
      tokenizerProvider.set(res)
    }
    return res
  }

  override fun computeBlocks(text: Content, delegate: CodeBlockAnalyzeDelegate): List<CodeBlock> {
    val stack = Stack<CodeBlock>()
    val blocks = ArrayList<CodeBlock>()
    var maxSwitch = 0
    var currSwitch = 0
    val brackets = SimpleBracketsCollector()
    val bracketsStack = Stack<Long>()
    var i = 0
    while (i < text.lineCount && delegate.isNotCancelled) {
      val state = getState(i)
      val checkForIdentifiers =
        state.state!!.state == STATE_NORMAL || (state.state!!.state == STATE_INCOMPLETE_COMMENT && state.tokens.size > 1)
      if (state.state!!.hasBraces || checkForIdentifiers) {
        // Iterate tokens
        for (i1 in state.tokens.indices) {
          val tokenRecord = state.tokens[i1]
          val token = tokenRecord!!.token
          if (token == Tokens.LBRACE) {
            val offset = tokenRecord.offset
            if (stack.isEmpty()) {
              if (currSwitch > maxSwitch) {
                maxSwitch = currSwitch
              }
              currSwitch = 0
            }
            currSwitch++
            val block = CodeBlock()
            block.startLine = i
            block.startColumn = offset
            stack.push(block)
          } else if (token == Tokens.RBRACE) {
            val offset = tokenRecord.offset
            if (!stack.isEmpty()) {
              val block = stack.pop()
              block.endLine = i
              block.endColumn = offset
              if (block.startLine != block.endLine) {
                blocks.add(block)
              }
            }
          }
          val type = getType(token)
          if (type > 0) {
            if (isStart(token)) {
              bracketsStack.push(IntPair.pack(type, text.getCharIndex(i, tokenRecord.offset)))
            } else {
              if (!bracketsStack.isEmpty()) {
                var record = bracketsStack.pop()
                val typeRecord = IntPair.getFirst(record!!)
                if (typeRecord == type) {
                  brackets.add(IntPair.getSecond(record), text.getCharIndex(i, tokenRecord.offset))
                } else if (type == 3) {
                  // Bad syntax, try to find type 3
                  while (!bracketsStack.isEmpty()) {
                    record = bracketsStack.pop()
                    if (IntPair.getFirst(record) == 3) {
                      brackets.add(
                        IntPair.getSecond(record),
                        text.getCharIndex(i, tokenRecord.offset)
                      )
                      break
                    }
                  }
                }
              }
            }
          }
        }
      }
      i++
    }
    if (delegate.isNotCancelled) {
      withReceiver { r: StyleReceiver ->
        r.updateBracketProvider(
          this,
          brackets
        )
      }
    }
    return blocks
  }

  override fun getInitialState(): State {
    return State()
  }

  override fun stateEquals(state: State, another: State): Boolean {
    return state == another
  }

  override fun onAddState(state: State) {
    if (state.identifiers != null) {
      for (identifier in state.identifiers!!) {
        identifiers.identifierIncrease(identifier)
      }
    }
  }

  override fun onAbandonState(state: State) {
    if (state.identifiers != null) {
      for (identifier in state.identifiers!!) {
        identifiers.identifierDecrease(identifier)
      }
    }
  }

  override fun reset(content: ContentReference, extraArguments: Bundle) {
    super.reset(content, extraArguments)
    identifiers.clear()
  }

  override fun tokenizeLine(
    line: CharSequence,
    state: State,
    lineIndex: Int
  ): LineTokenizeResult<State, HighlightToken?> {
    val tokens = ArrayList<HighlightToken>()
    var newState = 0
    val stateObj = State()
    if (state.state == STATE_NORMAL) {
      newState = tokenizeNormal(line, 0, tokens, stateObj)
    } else if (state.state == STATE_INCOMPLETE_COMMENT) {
      val res = tryFillIncompleteComment(line, tokens)
      newState = IntPair.getFirst(res)
      newState = if (newState == STATE_NORMAL) {
        tokenizeNormal(line, IntPair.getSecond(res), tokens, stateObj)
      } else {
        STATE_INCOMPLETE_COMMENT
      }
    }
    if (tokens.isEmpty()) {
      tokens.add(HighlightToken(Tokens.UNKNOWN, 0))
    }
    stateObj.state = newState
    return LineTokenizeResult(stateObj, tokens)
  }

  /**
   * @return state and offset
   */
  private fun tryFillIncompleteComment(
    line: CharSequence,
    tokens: MutableList<HighlightToken>
  ): Long {
    var pre = '\u0000'
    var cur = '\u0000'
    var offset = 0
    while ((pre != '*' || cur != '/') && offset < line.length) {
      pre = cur
      cur = line[offset]
      offset++
    }
    if (pre == '*' && cur == '/') {
      if (offset < 1000) {
        detectHighlightUrls(line.subSequence(0, offset), 0, Tokens.LONG_COMMENT_COMPLETE, tokens)
      } else {
        tokens.add(HighlightToken(Tokens.LONG_COMMENT_COMPLETE, 0))
      }
      return IntPair.pack(STATE_NORMAL, offset)
    }
    if (offset < 1000) {
      detectHighlightUrls(line.subSequence(0, offset), 0, Tokens.LONG_COMMENT_INCOMPLETE, tokens)
    } else {
      tokens.add(HighlightToken(Tokens.LONG_COMMENT_INCOMPLETE, 0))
    }
    return IntPair.pack(STATE_INCOMPLETE_COMMENT, offset)
  }

  private fun tokenizeNormal(
    text: CharSequence,
    offset: Int,
    tokens: MutableList<HighlightToken>,
    st: State
  ): Int {
    val tokenizer = obtainTokenizer()
    tokenizer.reset(text)
    tokenizer.offset = offset
    var token: Tokens
    var state = STATE_NORMAL
    while ((tokenizer.nextToken().also { token = it }) != Tokens.EOF) {
      if (tokenizer.tokenLength < 1000 &&
        (token == Tokens.STRING || token == Tokens.LONG_COMMENT_COMPLETE || token == Tokens.LONG_COMMENT_INCOMPLETE || token == Tokens.LINE_COMMENT)
      ) {
        // detect possible URLs, if the token is not too long
        detectHighlightUrls(tokenizer.tokenText, tokenizer.offset, token, tokens)
        if (token == Tokens.LONG_COMMENT_INCOMPLETE) {
          state = STATE_INCOMPLETE_COMMENT
          break
        }
        continue
      }
      tokens.add(HighlightToken(token, tokenizer.offset))
      if (token == Tokens.LBRACE || token == Tokens.RBRACE) {
        st.hasBraces = true
      }
      if (token == Tokens.IDENTIFIER) {
        st.addIdentifier(tokenizer.tokenText)
      }
      if (token == Tokens.LONG_COMMENT_INCOMPLETE) {
        state = STATE_INCOMPLETE_COMMENT
        break
      }
    }
    return state
  }

  private fun detectHighlightUrls(
    tokenText: CharSequence,
    offset: Int,
    token: Tokens,
    tokens: MutableList<HighlightToken>
  ) {
    val matcher = URL_PATTERN.matcher(tokenText)
    var index = 0
    while (index < tokenText.length && matcher.find(index)) {
      val start = matcher.start()
      val end = matcher.end()
      if (start > index) {
        tokens.add(HighlightToken(token, offset + index))
      }
      tokens.add(HighlightToken(token, offset + start, matcher.group()))
      index = end
    }
    if (index != tokenText.length) {
      tokens.add(HighlightToken(token, offset + index))
    }
  }


  override fun generateSpansForLine(lineResult: LineTokenizeResult<State, HighlightToken?>): MutableList<Span> {
    val spans = ArrayList<Span>()
    val tokens = lineResult.tokens
    var previous = Tokens.UNKNOWN
    var classNamePrevious = false
    for (i in tokens.indices) {
      val tokenRecord = tokens[i] ?: return mutableListOf()
      val token = tokenRecord.token
      val offset = tokenRecord.offset
      var span: Span
      when (token) {
        Tokens.WHITESPACE, Tokens.NEWLINE -> span =
          SpanFactory.obtain(offset, TextStyle.makeStyle(EditorColorScheme.TEXT_NORMAL))

        Tokens.CHARACTER_LITERAL, Tokens.FLOATING_POINT_LITERAL, Tokens.INTEGER_LITERAL, Tokens.STRING -> {
          classNamePrevious = false
          span = SpanFactory.obtain(offset, TextStyle.makeStyle(EditorColorScheme.LITERAL, true))
        }

        Tokens.INT, Tokens.LONG, Tokens.BOOLEAN, Tokens.BYTE, Tokens.CHAR, Tokens.FLOAT, Tokens.DOUBLE, Tokens.SHORT, Tokens.VOID, Tokens.VAR -> {
          classNamePrevious = true
          span = SpanFactory.obtain(
            offset,
            TextStyle.makeStyle(EditorColorScheme.KEYWORD, 0, true, false, false)
          )
        }

        Tokens.ABSTRACT, Tokens.ASSERT, Tokens.CLASS, Tokens.DO, Tokens.FINAL, Tokens.FOR, Tokens.IF, Tokens.NEW, Tokens.PUBLIC, Tokens.PRIVATE, Tokens.PROTECTED, Tokens.PACKAGE, Tokens.RETURN, Tokens.STATIC, Tokens.SUPER, Tokens.SWITCH, Tokens.ELSE, Tokens.VOLATILE, Tokens.SYNCHRONIZED, Tokens.STRICTFP, Tokens.GOTO, Tokens.CONTINUE, Tokens.BREAK, Tokens.TRANSIENT, Tokens.TRY, Tokens.CATCH, Tokens.FINALLY, Tokens.WHILE, Tokens.CASE, Tokens.DEFAULT, Tokens.CONST, Tokens.ENUM, Tokens.EXTENDS, Tokens.IMPLEMENTS, Tokens.IMPORT, Tokens.INSTANCEOF, Tokens.INTERFACE, Tokens.NATIVE, Tokens.THIS, Tokens.THROW, Tokens.THROWS, Tokens.TRUE, Tokens.FALSE, Tokens.NULL, Tokens.SEALED, Tokens.PERMITS -> {
          classNamePrevious = false
          span = SpanFactory.obtain(
            offset,
            TextStyle.makeStyle(EditorColorScheme.KEYWORD, 0, true, false, false)
          )
        }

        Tokens.LINE_COMMENT, Tokens.LONG_COMMENT_COMPLETE, Tokens.LONG_COMMENT_INCOMPLETE -> span =
          SpanFactory.obtain(
            offset,
            TextStyle.makeStyle(EditorColorScheme.COMMENT, 0, false, true, false, true)
          )

        Tokens.IDENTIFIER -> {
          var type = EditorColorScheme.IDENTIFIER_NAME
          if (classNamePrevious) {
            type = EditorColorScheme.IDENTIFIER_VAR
            classNamePrevious = false
          } else {
            if (previous == Tokens.AT) {
              type = EditorColorScheme.ANNOTATION
            } else {
              // Peek next token
              var j = i + 1
              var next = Tokens.UNKNOWN
              label@ while (j < tokens.size) {
                next = tokens[j]?.token ?: break@label
                when (next) {
                  Tokens.WHITESPACE, Tokens.NEWLINE, Tokens.LONG_COMMENT_INCOMPLETE, Tokens.LONG_COMMENT_COMPLETE, Tokens.LINE_COMMENT -> {}
                  else -> break@label
                }
                j++
              }
              if (next == Tokens.LPAREN) {
                type = EditorColorScheme.FUNCTION_NAME
              } else {
                classNamePrevious = true
              }
            }
          }
          span = SpanFactory.obtain(offset, TextStyle.makeStyle(type))
        }

        else -> {
          if (token == Tokens.LBRACK || (token == Tokens.RBRACK && previous == Tokens.LBRACK)) {
            span = SpanFactory.obtain(offset, EditorColorScheme.OPERATOR.toLong())
            break
          }
          classNamePrevious = false
          span = SpanFactory.obtain(offset, EditorColorScheme.OPERATOR.toLong())
        }
      }
      when (token) {
        Tokens.LINE_COMMENT, Tokens.LONG_COMMENT_COMPLETE, Tokens.LONG_COMMENT_INCOMPLETE, Tokens.WHITESPACE, Tokens.NEWLINE -> {}
        else -> previous = token
      }
      if (tokenRecord.url != null) {
        span.setSpanExt(SpanExtAttrs.EXT_INTERACTION_INFO, SpanClickableUrl(tokenRecord.url!!))
        span.underlineColor = EditorColor(span.foregroundColorId)
      }
      spans.add(span)
    }
    return spans
  }

  class HighlightToken {
    var token: Tokens
    var offset: Int
    var url: String? = null

    constructor(token: Tokens, offset: Int) {
      this.token = token
      this.offset = offset
    }

    constructor(token: Tokens, offset: Int, url: String?) {
      this.token = token
      this.offset = offset
      this.url = url
    }
  }

  companion object {
    private const val STATE_NORMAL = 0
    private const val STATE_INCOMPLETE_COMMENT = 1
    private val URL_PATTERN: Pattern =
      Pattern.compile("https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)")

    private fun getType(token: Tokens): Int {
      if (token == Tokens.LBRACE || token == Tokens.RBRACE) {
        return 3
      }
      if (token == Tokens.LBRACK || token == Tokens.RBRACK) {
        return 2
      }
      if (token == Tokens.LPAREN || token == Tokens.RPAREN) {
        return 1
      }
      return 0
    }

    private fun isStart(token: Tokens): Boolean {
      return token == Tokens.LBRACE || token == Tokens.LBRACK || token == Tokens.LPAREN
    }
  }
}