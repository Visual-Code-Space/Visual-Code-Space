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

import io.github.rosemoe.sora.util.MyCharacter
import io.github.rosemoe.sora.util.TrieTree

/**
 * @author Rose
 */
open class JavaTextTokenizer(src: CharSequence?) {
  private var source: CharSequence
  protected var bufferLen: Int = 0
  var line: Int = 0
    private set
  var column: Int = 0
    private set
  var index: Int = 0
    private set
  var offset: Int = 0
  var tokenLength: Int = 0
    protected set
  var token: Tokens? = null
    private set
  private var lcCal = false

  private fun init() {
    line = 0
    column = 0
    tokenLength = 0
    index = 0
    token = Tokens.WHITESPACE
    lcCal = false
    this.bufferLen = source.length
  }

  fun setCalculateLineColumn(cal: Boolean) {
    this.lcCal = cal
  }

  fun pushBack(length: Int) {
    require(length <= tokenLength) { "pushBack length too large" }
    this.tokenLength -= length
  }

  private fun isIdentifierPart(ch: Char): Boolean {
    return MyCharacter.isJavaIdentifierPart(ch)
  }

  private fun isIdentifierStart(ch: Char): Boolean {
    return MyCharacter.isJavaIdentifierStart(ch)
  }

  val tokenText: CharSequence
    get() = source.subSequence(offset, offset + tokenLength)

  private fun charAt(i: Int): Char {
    return source[i]
  }

  private fun charAt(): Char {
    return source[offset + tokenLength]
  }

  fun nextToken(): Tokens {
    return nextTokenInternal().also { token = it }
  }

  private fun nextTokenInternal(): Tokens {
    if (lcCal) {
      var r = false
      for (i in offset until offset + tokenLength) {
        val ch = charAt(i)
        if (ch == '\r') {
          r = true
          line++
          column = 0
        } else if (ch == '\n') {
          if (r) {
            r = false
            continue
          }
          line++
          column = 0
        } else {
          r = false
          column++
        }
      }
    }
    index += tokenLength
    offset += tokenLength
    if (offset >= bufferLen) {
      return Tokens.EOF
    }
    val ch = source[offset]
    tokenLength = 1
    if (ch == '\n') {
      return Tokens.NEWLINE
    } else if (ch == '\r') {
      scanNewline()
      return Tokens.NEWLINE
    } else if (isWhitespace(ch)) {
      var chLocal: Char = 'v'
      while (
        offset + tokenLength < bufferLen &&
        isWhitespace(charAt(offset + tokenLength).also { chLocal = it })
      ) {
        if (chLocal == '\r' || chLocal == '\n') {
          break
        }
        tokenLength++
      }
      return Tokens.WHITESPACE
    } else {
      if (isIdentifierStart(ch)) {
        return scanIdentifier(ch)
      }
      if (isPrimeDigit(ch)) {
        return scanNumber()
      }
      /* Scan usual symbols first */
      when (ch) {
        ';' -> return Tokens.SEMICOLON
        '(' -> return Tokens.LPAREN
        ')' -> return Tokens.RPAREN
        ':' -> return Tokens.COLON
        '<' -> return scanLT()
        '>' -> return scanGT()
        /* Scan secondly symbols */
        else -> when (ch) {
          '=' -> return scanOperatorTwo(Tokens.EQ)
          '.' -> return Tokens.DOT
          '@' -> return Tokens.AT
          '{' -> return Tokens.LBRACE
          '}' -> return Tokens.RBRACE
          '/' -> return scanDIV()
          '*' -> return scanOperatorTwo(Tokens.MULT)
          '-' -> return scanOperatorTwo(Tokens.MINUS)
          '+' -> return scanOperatorTwo(Tokens.PLUS)
          '[' -> return Tokens.LBRACK
          ']' -> return Tokens.RBRACK
          ',' -> return Tokens.COMMA
          '!' -> return Tokens.NOT
          '~' -> return Tokens.COMP
          '?' -> return Tokens.QUESTION
          '&' -> return scanOperatorTwo(Tokens.AND)
          '|' -> return scanOperatorTwo(Tokens.OR)
          '^' -> return scanOperatorTwo(Tokens.XOR)
          '%' -> return scanOperatorTwo(Tokens.MOD)
          '\'' -> {
            scanCharLiteral()
            return Tokens.CHARACTER_LITERAL
          }

          '\"' -> {
            scanStringLiteral()
            return Tokens.STRING
          }

          else -> return Tokens.UNKNOWN
        }
      }
    }
  }

  protected fun throwIfNeeded() {
    if (offset + tokenLength == bufferLen) {
      throw RuntimeException("Token too long")
    }
  }

  protected fun scanNewline() {
    if (offset + tokenLength < bufferLen && charAt(offset + tokenLength) == '\n') {
      tokenLength++
    }
  }

  @Suppress("NAME_SHADOWING")
  protected fun scanIdentifier(ch: Char): Tokens {
    var ch = ch
    var n = tree!!.root.map[ch]
    while (offset + tokenLength < bufferLen && isIdentifierPart(charAt(offset + tokenLength).also {
        ch = it
      })) {
      tokenLength++
      n = n?.map?.get(ch)
    }
    return if (n == null) Tokens.IDENTIFIER else ((if (n.token == null) Tokens.IDENTIFIER else n.token)!!)
  }

  protected fun scanTrans() {
    throwIfNeeded()
    val ch = charAt()
    if (ch == '\\' || ch == 't' || ch == 'f' || ch == 'n' || ch == 'r' || ch == '0' || ch == '\"' || ch == '\'' || ch == 'b') {
      tokenLength++
    } else if (ch == 'u') {
      tokenLength++
      for (i in 0..3) {
        throwIfNeeded()
        if (!isDigit(charAt(offset + tokenLength))) {
          return
        }
        tokenLength++
      }
    }
  }

  protected fun scanStringLiteral() {
    throwIfNeeded()
    var ch: Char = 'v'
    while (offset + tokenLength < bufferLen && (charAt(offset + tokenLength).also {
        ch = it
      }) != '\"') {
      if (ch == '\\') {
        tokenLength++
        scanTrans()
      } else {
        if (ch == '\n') {
          return
        }
        tokenLength++
      }
    }
    if (offset + tokenLength < bufferLen) {
      tokenLength++
    }
  }

  protected fun scanCharLiteral() {
    throwIfNeeded()
    var ch: Char = 'v'
    while (offset + tokenLength < bufferLen && (charAt(offset + tokenLength).also {
        ch = it
      }) != '\'') {
      if (ch == '\\') {
        tokenLength++
        scanTrans()
      } else {
        if (ch == '\n') {
          return
        }
        tokenLength++
        throwIfNeeded()
      }
    }
    if (offset + tokenLength != bufferLen) {
      tokenLength++
    }
  }

  protected fun scanNumber(): Tokens {
    if (offset + tokenLength == bufferLen) {
      return Tokens.INTEGER_LITERAL
    }
    var flag = false
    var ch = charAt(offset)
    if (ch == '0') {
      if (charAt() == 'x') {
        tokenLength++
      }
      flag = true
    }
    while (offset + tokenLength < bufferLen && isDigit(charAt())) {
      tokenLength++
    }
    if (offset + tokenLength == bufferLen) {
      return Tokens.INTEGER_LITERAL
    }
    ch = charAt()
    if (ch == '.') {
      if (flag) {
        return Tokens.INTEGER_LITERAL
      }
      if (offset + tokenLength + 1 == bufferLen) {
        return Tokens.INTEGER_LITERAL
      }
      tokenLength++
      throwIfNeeded()
      while (offset + tokenLength < bufferLen && isDigit(charAt())) {
        tokenLength++
      }
      if (offset + tokenLength == bufferLen) {
        return Tokens.FLOATING_POINT_LITERAL
      }
      ch = charAt()
      if (ch == 'e' || ch == 'E') {
        tokenLength++
        throwIfNeeded()
        if (charAt() == '-' || charAt() == '+') {
          tokenLength++
          throwIfNeeded()
        }
        while (offset + tokenLength < bufferLen && isPrimeDigit(charAt())) {
          tokenLength++
        }
        if (offset + tokenLength == bufferLen) {
          return Tokens.FLOATING_POINT_LITERAL
        }
        ch = charAt()
      }
      if (ch == 'f' || ch == 'F' || ch == 'D' || ch == 'd') {
        tokenLength++
      }
      return Tokens.FLOATING_POINT_LITERAL
    } else if (ch == 'l' || ch == 'L') {
      tokenLength++
      return Tokens.INTEGER_LITERAL
    } else if (ch == 'F' || ch == 'f' || ch == 'D' || ch == 'd') {
      tokenLength++
      return Tokens.FLOATING_POINT_LITERAL
    } else {
      return Tokens.INTEGER_LITERAL
    }
  }

  /* The following methods have been simplified for syntax high light */
  protected fun scanDIV(): Tokens {
    if (offset + 1 == bufferLen) {
      return Tokens.DIV
    }
    val ch = charAt()
    if (ch == '/') {
      tokenLength++
      while (offset + tokenLength < bufferLen && charAt() != '\n') {
        tokenLength++
      }
      return Tokens.LINE_COMMENT
    } else if (ch == '*') {
      tokenLength++
      var pre: Char
      var curr = '?'
      var finished = false
      while (offset + tokenLength < bufferLen) {
        pre = curr
        curr = charAt()
        if (curr == '/' && pre == '*') {
          tokenLength++
          finished = true
          break
        }
        tokenLength++
      }
      return if (finished) Tokens.LONG_COMMENT_COMPLETE else Tokens.LONG_COMMENT_INCOMPLETE
    } else {
      return Tokens.DIV
    }
  }

  protected fun scanLT(): Tokens {
    return Tokens.LT
  }

  protected fun scanGT(): Tokens {
    return Tokens.GT
  }

  protected fun scanOperatorTwo(ifWrong: Tokens): Tokens {
    return ifWrong
  }

  fun reset(src: CharSequence) {
    this.source = src
    line = 0
    column = 0
    tokenLength = 0
    index = 0
    offset = 0
    token = Tokens.WHITESPACE
    bufferLen = src.length
  }

  init {
    requireNotNull(src) { "src can not be null" }
    this.source = src
    init()
  }

  companion object {
    var tree: TrieTree<Tokens?>? = null
      private set

    var sKeywords: MutableList<String> = mutableListOf()

    init {
      doStaticInit()
    }

    protected fun doStaticInit() {
      sKeywords.addAll(
        listOf(
          "abstract", "assert", "boolean", "byte", "char", "class", "do",
          "double", "final", "float", "for", "if", "int", "long", "new",
          "public", "private", "protected", "package", "return", "static",
          "short", "super", "switch", "else", "volatile", "synchronized", "strictfp",
          "goto", "continue", "break", "transient", "void", "try", "catch",
          "finally", "while", "case", "default", "const", "enum", "extends",
          "implements", "import", "instanceof", "interface", "native",
          "this", "throw", "throws", "true", "false", "null", "var", "sealed", "permits"
        )
      )
      val sTokens = arrayOf(
        Tokens.ABSTRACT,
        Tokens.ASSERT,
        Tokens.BOOLEAN,
        Tokens.BYTE,
        Tokens.CHAR,
        Tokens.CLASS,
        Tokens.DO,
        Tokens.DOUBLE,
        Tokens.FINAL,
        Tokens.FLOAT,
        Tokens.FOR,
        Tokens.IF,
        Tokens.INT,
        Tokens.LONG,
        Tokens.NEW,
        Tokens.PUBLIC,
        Tokens.PRIVATE,
        Tokens.PROTECTED,
        Tokens.PACKAGE,
        Tokens.RETURN,
        Tokens.STATIC,
        Tokens.SHORT,
        Tokens.SUPER,
        Tokens.SWITCH,
        Tokens.ELSE,
        Tokens.VOLATILE,
        Tokens.SYNCHRONIZED,
        Tokens.STRICTFP,
        Tokens.GOTO,
        Tokens.CONTINUE,
        Tokens.BREAK,
        Tokens.TRANSIENT,
        Tokens.VOID,
        Tokens.TRY,
        Tokens.CATCH,
        Tokens.FINALLY,
        Tokens.WHILE,
        Tokens.CASE,
        Tokens.DEFAULT,
        Tokens.CONST,
        Tokens.ENUM,
        Tokens.EXTENDS,
        Tokens.IMPLEMENTS,
        Tokens.IMPORT,
        Tokens.INSTANCEOF,
        Tokens.INTERFACE,
        Tokens.NATIVE,
        Tokens.THIS,
        Tokens.THROW,
        Tokens.THROWS,
        Tokens.TRUE,
        Tokens.FALSE,
        Tokens.NULL,
        Tokens.VAR,
        Tokens.SEALED,
        Tokens.PERMITS
      )
      tree = TrieTree()
      for (i in sKeywords.indices) {
        tree!!.put(
          sKeywords[i],
          sTokens[i]
        )
      }
    }
  }

  protected fun isDigit(c: Char): Boolean {
    return ((c in '0'..'9') || (c in 'A'..'F') || (c in 'a'..'f'))
  }

  protected fun isPrimeDigit(c: Char): Boolean {
    return (c in '0'..'9')
  }

  protected fun isWhitespace(c: Char): Boolean {
    return (c == '\t' || c == ' ' || c == '\u000c' || c == '\n' || c == '\r')
  }
}