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
import com.raredev.vcspace.editor.ace.VCSpaceEditorLanguage
import com.raredev.vcspace.editor.ace.VCSpaceEditorTheme
import com.raredev.vcspace.editor.ace.VCSpaceWebChromeClient
import com.raredev.vcspace.editor.ace.VCSpaceWebInterface
import com.raredev.vcspace.editor.ace.VCSpaceWebViewClient
import com.raredev.vcspace.editor.ace.options.EditorOptions
import java.io.File

class AceCodeEditor : WebView {
  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
  
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
    loadUrl("file:///android_asset/ace-editor/editor.html")
  }
  
  fun release() {
    destroy()
  }
  
  fun loadOptions() {
    val sb = StringBuilder().apply {
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
      append("mergeUndoDeltas: ${when (options.mergeUndoDeltas) {
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

  fun setText(text: String) = loadJs("editor.session.setValue('${text.replace("\n", "\\n").replace("\r", "\\r").replace("'", "\\'")}')")

  fun getText() = webInterface.value

  fun setFontSize(fontSizeInPx: Int) = loadJs("editor.setFontSize($fontSizeInPx)")

  fun getFontSize(callback: OnJsReturnValueCallback) = loadJs("editor.getFontSize()", callback)

  fun setShowPrintMargin(showPrintMargin: Boolean) = loadJs("editor.setShowPrintMargin($showPrintMargin);")

  fun setHighlightActiveLine(highlightActiveLine: Boolean) = loadJs("editor.setHighlightActiveLine($highlightActiveLine);")

  fun setReadOnly(readOnly: Boolean) = loadJs("editor.setReadOnly($readOnly);")

  fun setUseWrapMode(useWrapMode: Boolean) = loadJs("editor.getSession().setUseWrapMode($useWrapMode);")

  fun setUseSoftTabs(useSoftTabs: Boolean) = loadJs("editor.getSession().setUseSoftTabs($useSoftTabs);")

  fun setTabSize(tabSize: Int) = loadJs("editor.getSession().setTabSize($tabSize);")

  fun gotoLine(lineNumber: Int) = loadJs("editor.gotoLine($lineNumber);")

  fun getTotalLines(callback: OnJsReturnValueCallback) = loadJs("editor.session.getLength()", callback)

  fun insert(text: String) = loadJs("editor.insert(\"${text.replace("\n", "\\n").replace("\r", "\\r").replace("'", "\\'")}\");")

  fun setLanguageFromFile(filePath: String?) = loadJs("setLanguageFromFile(\"$filePath\");")

  fun moveCursorTo(row: Int, column: Int) = loadJs("editor.session.getSelection().moveCursorTo($row, $column);")

  fun undo() = loadJs("editor.session.getUndoManager().undo()")

  fun redo() = loadJs("editor.session.getUndoManager().redo()")
  
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