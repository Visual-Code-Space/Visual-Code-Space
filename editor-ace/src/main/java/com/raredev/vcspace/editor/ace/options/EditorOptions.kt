package com.raredev.vcspace.editor.ace.options

import com.raredev.vcspace.editor.ace.VCSpaceEditorLanguage
import com.raredev.vcspace.editor.ace.VCSpaceEditorTheme
import com.raredev.vcspace.utils.PreferencesUtils as settings

class EditorOptions {
  companion object {
    val instance: EditorOptions by lazy { EditorOptions() }
  }

  var selectionStyle = SelectionStyle.TEXT
  var cursorStyle = CursorStyle.SMOOTH
  var theme = VCSpaceEditorTheme.GITHUB_DARK
  var language = VCSpaceEditorLanguage.TEXT
  var newLineMode = NewLineMode.AUTO
  var foldStyle = FoldStyle.MARKBEGIN
  var mergeUndoDeltas = MergeUndoDeltas.ALWAYS

  var highlightActiveLine = true
  var highlightSelectedWord = false
  var readOnly = false
  var enableAutoIndent = true
  var enableBasicAutocompletion = true
  var enableLiveAutocompletion = true
  var enableSnippets = true
  // var autoScrollEditorIntoView = true
  var hScrollBarAlwaysVisible = false
  var vScrollBarAlwaysVisible = false
  var highlightGutterLine = true
  var animatedScroll = false
  var showInvisibles = false
  var showPrintMargin = true
  var fadeFoldWidgets = false
  var showFoldWidgets = true
  var showLineNumbers = settings.lineNumbers
  var showGutter = true
  var displayIndentGuides = true
  var fixedWidthGutter = false
  var useSvgGutterIcons = false
  var useSoftTabs = !settings.useTab
  var enableEmmet = false
  var useElasticTabstops = false

  var firstLineNumber = 1
  var fontSize = settings.textSize
  var printMarginColumn = 80
  var tabSize = settings.tabSize
  var maxLines = 0
  var minLines = 0

  var scrollPastEnd = 0.5f // typical values are 0, 0.5, and 1

  fun printMargin(printMarginColumn: Int) {
    showPrintMargin = true
    this.printMarginColumn = printMarginColumn
  }

  enum class CursorStyle {
    ACE,
    SLIM,
    SMOOTH,
    WIDE
  }

  enum class SelectionStyle {
    LINE,
    TEXT
  }

  enum class NewLineMode {
    AUTO,
    UNIX,
    WINDOWS
  }

  enum class FoldStyle {
    MARKBEGIN,
    MARKBEGINEND,
    MANUAL
  }

  enum class MergeUndoDeltas {
    ALWAYS,
    NEVER,
    TIMED
  }
}
