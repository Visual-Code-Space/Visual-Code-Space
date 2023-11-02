package com.raredev.vcspace.editor

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.webkit.WebView
import com.raredev.vcspace.editor.ace.OnEditorLoadedListener
import com.raredev.vcspace.editor.ace.OnJsReturnValueCallback
import com.raredev.vcspace.editor.ace.Point
import com.raredev.vcspace.editor.ace.VCSpaceEditorLanguage
import com.raredev.vcspace.editor.ace.VCSpaceEditorTheme
import com.raredev.vcspace.editor.ace.VCSpaceWebChromeClient
import com.raredev.vcspace.editor.ace.VCSpaceWebInterface
import com.raredev.vcspace.editor.ace.VCSpaceWebViewClient
import com.raredev.vcspace.editor.ace.options.EditorOptions
import com.raredev.vcspace.editor.ace.options.SearchOptions
import java.io.File

class AceCodeEditor : WebView {
  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

  constructor(
      context: Context,
      attrs: AttributeSet?,
      defStyleAttr: Int
  ) : super(context, attrs, defStyleAttr)

  constructor(
      context: Context,
      attrs: AttributeSet?,
      defStyleAttr: Int,
      defStyleRes: Int
  ) : super(context, attrs, defStyleAttr, defStyleRes)

  var onEditorLoadedListener: OnEditorLoadedListener? = null

  var webInterface: VCSpaceWebInterface
  var options: EditorOptions
  var isModified: Boolean = false
  var file: File? = null

  init {
    options = EditorOptions.instance
    webInterface = VCSpaceWebInterface(this)
    addJavascriptInterface(webInterface, "VCSpace")
    webChromeClient = VCSpaceWebChromeClient()
    webViewClient = VCSpaceWebViewClient(this)

    settings.javaScriptEnabled = true
    loadUrl("file:///android_asset/editor/ace-editor/editor.html")
  }

  fun release() {
    destroyEditor()
    destroy()
  }

  fun loadOptions() {
    val sb =
        StringBuilder().apply {
          append("editor.setOptions({")
          append("highlightActiveLine: ${options.highlightActiveLine},")
          append("highlightSelectedWord: ${options.highlightSelectedWord},")
          append("readOnly: ${options.readOnly},")
          append("enableAutoIndent: ${options.enableAutoIndent},")
          append("enableBasicAutocompletion: ${options.enableBasicAutocompletion},")
          append("enableLiveAutocompletion: ${options.enableLiveAutocompletion},")
          append("enableSnippets: ${options.enableSnippets},")
          append("hScrollBarAlwaysVisible: ${options.hScrollBarAlwaysVisible},")
          append("vScrollBarAlwaysVisible: ${options.vScrollBarAlwaysVisible},")
          append("highlightGutterLine: ${options.highlightGutterLine},")
          append("animatedScroll: ${options.animatedScroll},")
          append("showInvisibles: ${options.showInvisibles},")
          append("showPrintMargin: ${options.showPrintMargin},")
          append("fadeFoldWidgets: ${options.fadeFoldWidgets},")
          append("showFoldWidgets: ${options.showFoldWidgets},")
          append("showLineNumbers: ${options.showLineNumbers},")
          append("showGutter: ${options.showGutter},")
          append("displayIndentGuides: ${options.displayIndentGuides},")
          append("fixedWidthGutter: ${options.fixedWidthGutter},")
          append("useSvgGutterIcons: ${options.useSvgGutterIcons},")
          append("useSoftTabs: ${options.useSoftTabs},")
          append("enableEmmet: ${options.enableEmmet},")
          append(
              "mergeUndoDeltas: ${when (options.mergeUndoDeltas) {
        EditorOptions.MergeUndoDeltas.NEVER -> false
        EditorOptions.MergeUndoDeltas.TIMED -> true
        else -> "'always'"
        }},")
          append("selectionStyle: \"${options.selectionStyle.name.lowercase()}\",")
          append("cursorStyle: \"${options.cursorStyle.name.lowercase()}\",")
          append("theme: \"ace/theme/${options.theme.name.lowercase()}\",")
          append("mode: \"ace/mode/${options.language.name.lowercase()}\",")
          append("newLineMode: \"${options.newLineMode.name.lowercase()}\",")
          append("foldStyle: \"${options.foldStyle.name.lowercase()}\",")
          append("firstLineNumber: ${options.firstLineNumber},")
          append("fontSize: ${options.fontSize},")
          append("printMarginColumn: ${options.printMarginColumn},")
          append("tabSize: ${options.tabSize},")
          append("maxLines: ${if (options.maxLines == 0) "undefined" else options.maxLines},")
          append("minLines: ${if (options.minLines == 0) "undefined" else options.minLines},")
          append("scrollPastEnd: ${options.scrollPastEnd}")
          append("})")
        }
    loadJs(sb.toString())
  }

  fun setTheme(theme: VCSpaceEditorTheme) {
    loadJs("editor.setTheme('ace/theme/${theme.name.lowercase()}')")
  }

  fun setLanguage(language: VCSpaceEditorLanguage) {
    loadJs("editor.session.setMode('ace/mode/${language.name.lowercase()}')")
  }

  fun hasUndo() = webInterface.hasUndo

  fun hasRedo() = webInterface.hasRedo

  fun setText(text: String) =
      loadJs(
          "editor.session.setValue('${text.replace("\n", "\\n").replace("\r", "\\r").replace("'", "\\'")}')")

  fun getText() = webInterface.value

  fun setFontSize(fontSizeInPx: Int) = loadJs("editor.setFontSize($fontSizeInPx)")

  fun getFontSize(callback: OnJsReturnValueCallback) = loadJs("editor.getFontSize()", callback)

  fun setShowPrintMargin(showPrintMargin: Boolean) =
      loadJs("editor.setShowPrintMargin($showPrintMargin);")

  fun setHighlightActiveLine(highlightActiveLine: Boolean) =
      loadJs("editor.setHighlightActiveLine($highlightActiveLine);")

  fun setReadOnly(readOnly: Boolean) = loadJs("editor.setReadOnly($readOnly);")

  fun setUseWrapMode(useWrapMode: Boolean) = loadJs("editor.session.setUseWrapMode($useWrapMode);")

  fun setUseSoftTabs(useSoftTabs: Boolean) = loadJs("editor.session.setUseSoftTabs($useSoftTabs);")

  fun setTabSize(tabSize: Int) = loadJs("editor.session.setTabSize($tabSize);")

  fun gotoLine(lineNumber: Int) = loadJs("editor.gotoLine($lineNumber);")

  fun getTotalLines(callback: OnJsReturnValueCallback) =
      loadJs("editor.session.getLength()", callback)

  fun insert(text: String) =
      loadJs(
          "editor.insert(\"${text.replace("\n", "\\n").replace("\r", "\\r").replace("'", "\\'")}\");")

  fun setLanguageFromFile(filePath: String?) = loadJs("setLanguageFromFile(\"$filePath\");")

  fun undo() = loadJs("editor.session.getUndoManager().undo()")

  fun redo() = loadJs("editor.session.getUndoManager().redo()")

  fun destroyEditor() = loadJs("editor.session.destroy()")

  fun alignCursors() = loadJs("editor.alignCursors()")

  fun applyComposition(text: Any?, composition: Any?) =
      loadJs("editor.applyComposition($text, $composition)")

  fun autoIndent() = loadJs("editor.autoIndent()")

  fun blockIndent() = loadJs("editor.blockIndent()")

  fun blockOutdent() = loadJs("editor.blockOutdent()")

  fun blur() = loadJs("editor.blur()")

  fun centerSelection() = loadJs("editor.centerSelection()")

  fun copyLinesDown() = loadJs("editor.copyLinesDown()")

  fun copyLinesUp() = loadJs("editor.copyLinesUp()")

  fun duplicateSelection() = loadJs("editor.duplicateSelection()")
  // fun endOperation(e: Any? = null) = loadJs("editor.endOperation()")

  fun find(needle: String, options: SearchOptions? = null, animate: Boolean = true) {
    val optionsString =
        options?.let {
          val parts = mutableListOf<String>()
          with(it) {
            if (preventScroll) parts += "preventScroll: true"
            if (backwards) parts += "backwards: true"
            // TODO:
            // start?.let {
            //   parts += "start: {start: {row: ${it.start.row}, column: ${it.start.column}}, end:
            // {row: ${it.end.row}, column: ${it.end.column}}}"
            // }
            // range?.let {
            //   parts += "range: {start: {row: ${it.start.row}, column: ${it.start.column}}, end:
            // {row: ${it.end.row}, column: ${it.end.column}}}"
            // }
            if (skipCurrent) parts += "skipCurrent: true"
            if (preserveCase) parts += "preserveCase: true"
            if (regExp) parts += "regExp: true"
            if (wholeWord) parts += "wholeWord: true"
            if (caseSensitive) parts += "caseSensitive: true"
            if (wrap) parts += "wrap: true"
          }
          if (parts.isNotEmpty()) ", {${parts.joinToString(", ")}}" else ""
        } ?: ""

    val animateString = if (animate) "true" else "false"
    val js = "editor.find('$needle'$optionsString, $animateString)"
    loadJs(js)
  }

  fun findAll(needle: String, options: SearchOptions? = null, additive: Boolean = false) {
    // TODO: Implement this method
  }

  fun findLinkAt(row: Int, column: Int) = loadJs("editor.findLinkAt($row, $column)")

  fun findNext(options: SearchOptions? = null, animate: Boolean = false) {
    // TODO: Implement this method
  }

  fun findPrevious(options: SearchOptions? = null, animate: Boolean = false) {
    // TODO: Implement this method
  }

  fun focus() = loadJs("editor.focus()")

  fun getCursorPosition(): Point? = webInterface.cursorPosition

  fun jumpToMatching(select: Boolean, expand: Boolean) =
      loadJs("editor.jumpToMatching($select, $expand")

  fun moveCursorTo(row: Int, column: Int) = loadJs("editor.moveCursorTo($row, $column)")

  fun moveCursorToPosition(pos: Point): Unit = moveCursorTo(pos.row, pos.column)

  fun moveLinesDown() = loadJs("editor.moveLinesDown()")

  fun moveLinesUp() = loadJs("editor.moveLinesUp()")

  fun navigateFileEnd() = loadJs("editor.navigateFileEnd()")

  fun navigateFileStart() = loadJs("editor.navigateFileStart()")

  fun navigateLineEnd() = loadJs("editor.navigateLineEnd()")

  fun navigateLineStart() = loadJs("editor.navigateLineStart()")

  fun replace(replacement: String, options: SearchOptions? = null) {
    // TODO: Implement this method
  }

  fun replaceAll(replacement: String, options: SearchOptions? = null) {
    // TODO: Implement this method
  }

  fun resize(force: Boolean = false) = loadJs("editor.resize($force)")

  fun selectAll() = loadJs("editor.selectAll()")

  fun showKeyboardShortcuts() = loadJs("editor.showKeyboardShortcuts()")

  fun showSettingsMenu() = loadJs("editor.showSettingsMenu()")

  fun toggleBlockComment() = loadJs("editor.toggleBlockComment()")

  fun loadJs(js: String, callback: OnJsReturnValueCallback? = null) {
    evaluateJavascript(js) { value -> callback?.onReturnValue(value) }
  }

  override fun dispatchKeyEvent(event: KeyEvent): Boolean {
    return super.dispatchKeyEvent(event)
  }

  override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
    // this is needed for #dispatchKeyEvent() to be notified.
    return BaseInputConnection(this, false)
  }
}
