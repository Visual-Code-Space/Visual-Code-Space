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

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.itsvks.monaco.option.AcceptSuggestionOnEnter
import com.itsvks.monaco.option.MatchBrackets
import com.itsvks.monaco.option.Option
import com.itsvks.monaco.option.Position
import com.itsvks.monaco.option.TextEditorCursorBlinkingStyle
import com.itsvks.monaco.option.TextEditorCursorStyle
import com.itsvks.monaco.option.WordBreak
import com.itsvks.monaco.option.WordWrap
import com.itsvks.monaco.option.WrappingStrategy
import com.itsvks.monaco.option.minimap.MinimapOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A WebView-based Monaco Editor component for Android.
 *
 * This class provides a way to embed the Monaco Editor within an Android application,
 * allowing for rich code editing capabilities. It uses a WebView to display the editor
 * and communicates with it using JavaScript.
 *
 * Configure editor only when editor is loaded successfully. see [addOnEditorLoadCallback]
 *
 * @param context The application context.
 * @param attrs The attribute set for the view.
 *
 * @author Vivek
 */
@SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
class MonacoEditor @JvmOverloads constructor(
  context: Context, attrs: AttributeSet? = null
) : WebView(context, attrs) {
  companion object {
    private const val TAG = "MonacoEditor"
  }

  private val client = MonacoEditorClient(this)
  private val webInterface = MonacoWebInterface(this)

  var isLoaded = false
  var onEditorLoadCallbacks = mutableListOf<(MonacoEditor) -> Unit>()
  var onContentChange: (String) -> Unit = {}

  val editorScope = CoroutineScope(Dispatchers.Unconfined)

  init {
    addJavascriptInterface(webInterface, "MonacoAndroid")
    webViewClient = client
    webChromeClient = object : WebChromeClient() {
      override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
        @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
        when (consoleMessage.messageLevel()) {
          ConsoleMessage.MessageLevel.LOG -> Log.d(TAG, consoleMessage.message())
          ConsoleMessage.MessageLevel.DEBUG -> Log.d(TAG, consoleMessage.message())
          ConsoleMessage.MessageLevel.TIP -> Log.i(TAG, consoleMessage.message())
          ConsoleMessage.MessageLevel.WARNING -> Log.w(TAG, consoleMessage.message())

          ConsoleMessage.MessageLevel.ERROR -> {
            Log.e(TAG, consoleMessage.message())
            // ToastUtils.showShort(consoleMessage.message())
            // insert(consoleMessage.message(), position)
          }
        }
        return super.onConsoleMessage(consoleMessage)
      }
    }

    isFocusable = true
    isFocusableInTouchMode = true
    requestFocus(View.FOCUS_DOWN)

    settings.apply {
      javaScriptEnabled = true
      loadWithOverviewMode = true
      useWideViewPort = true
      allowFileAccess = true
      allowContentAccess = true
      setWebContentsDebuggingEnabled(true)
    }

    loadUrl("https://appassets.androidplatform.net/assets/code-oss/editor/index.html")

    setOnTouchListener { _, event ->
      if (event.actionMasked == MotionEvent.ACTION_UP) {
        requestFocus()
        //editorScope.launch { focusEditor() }
      }
      false
    }
  }

  fun addOnEditorLoadCallback(callback: (MonacoEditor) -> Unit) {
    onEditorLoadCallbacks.add(callback)
    Log.i(TAG, "addOnEditorLoadCallback: $callback")
    Log.i(TAG, "Total callbacks: ${onEditorLoadCallbacks.size}, $onEditorLoadCallbacks")
  }

  override fun reload() {
    loadUrl("https://appassets.androidplatform.net/assets/code-oss/editor/index.html")
  }

  private fun loadJs(script: String, resultCallback: (String) -> Unit = {}) {
    editorScope.launch {
      withContext(Dispatchers.Unconfined) {
        evaluateJavascript(script, resultCallback)
      }
    }
  }

  private fun setEditorOptionInternal(option: String, value: Any) {
    loadJs("setEditorOptions(`$option`, `$value`);")
  }

  fun <T : Option<Any>> setEditorOption(option: IEditorOption<T>) {
    setEditorOptionInternal(option.option.name, option.defaultValue.value)
  }

  fun <T : Option<Any>> setEditorOption(option: EditorOption, value: T) {
    setEditorOption(IEditorOption(option, value))
  }

  fun setEditorOption(option: EditorOption, value: String) {
    setEditorOptionInternal(option.name, value)
  }

  fun setEditorOption(option: EditorOption, value: Number) {
    setEditorOptionInternal(option.name, value)
  }

  fun setEditorOption(option: EditorOption, value: Boolean) {
    setEditorOptionInternal(option.name, value)
  }

  fun focusEditor() {
    loadJs("focusEditor();")
  }

  fun undo() = editorScope.launch { loadJs("undo();") }
  fun redo() = editorScope.launch { loadJs("redo();") }

  var text
    get() = webInterface.value
    set(text) {
      webInterface.value = text
      loadJs("setText(`$text`);")
    }
  val canUndo get() = webInterface.canUndo
  val canRedo get() = webInterface.canRedo
  val isContentModified = webInterface.isModified

  val position get() = Position(webInterface.lineNumber, webInterface.column)

  fun setLanguage(language: MonacoLanguage) = loadJs("setLanguage(`${language.value}`);")

  fun setFontSize(fontSize: Int) = setEditorOption(EditorOption.fontSize, fontSize)

  fun setTheme(theme: MonacoTheme) = loadJs("setTheme(`${theme.value}`);")

  fun setWordWrap(wordWrap: WordWrap) {
    setEditorOption(EditorOption.wordWrap, wordWrap)
  }

  fun setWrappingStrategy(wrappingStrategy: WrappingStrategy) {
    setEditorOption(EditorOption.wrappingStrategy, wrappingStrategy)
  }

  fun setWordBreak(wordBreak: WordBreak) {
    setEditorOption(EditorOption.wordBreak, wordBreak)
  }

  fun setMatchBrackets(matchBrackets: MatchBrackets) {
    setEditorOption(EditorOption.matchBrackets, matchBrackets)
  }

  fun setCursorStyle(cursorStyle: TextEditorCursorStyle) {
    loadJs("setCursorStyle(${cursorStyle.value});")
  }

  fun setReadOnly(readOnly: Boolean) {
    setEditorOption(EditorOption.readOnly, readOnly)
  }

  fun setMinimapOptions(minimapOptions: MinimapOptions) {
    loadJs("applyMinimapOptions(`${minimapOptions.toJson()}`);")
  }

  fun setGlyphMargin(glyphMargin: Boolean) {
    setEditorOption(EditorOption.glyphMargin, glyphMargin)
  }

  fun setFolding(folding: Boolean) = setEditorOption(EditorOption.folding, folding)

  fun setInDiffEditor(inDiffEditor: Boolean) {
    setEditorOption(EditorOption.inDiffEditor, inDiffEditor)
  }

  fun setLetterSpacing(letterSpacing: Number) {
    setEditorOption(EditorOption.letterSpacing, letterSpacing)
  }

  fun setLineDecorationsWidth(lineDecorationsWidth: Number) {
    setEditorOption(EditorOption.lineDecorationsWidth, lineDecorationsWidth)
  }

  fun setLineNumbersMinChars(lineNumbersMinChars: Number) {
    setEditorOption(EditorOption.lineNumbersMinChars, lineNumbersMinChars)
  }

  fun setAcceptSuggestionOnCommitCharacter(acceptSuggestionOnCommitCharacter: Boolean) {
    setEditorOption(
      EditorOption.acceptSuggestionOnCommitCharacter,
      acceptSuggestionOnCommitCharacter
    )
  }

  fun setAcceptSuggestionOnEnter(acceptSuggestionOnEnter: AcceptSuggestionOnEnter) {
    setEditorOption(EditorOption.acceptSuggestionOnEnter, acceptSuggestionOnEnter)
  }

  fun setCursorBlinkingStyle(cursorBlinkingStyle: TextEditorCursorBlinkingStyle) {
    loadJs("setCursorBlinkingStyle(${cursorBlinkingStyle.value});")
  }

  fun insert(text: CharSequence, position: Position) {
    loadJs("insert(`$text`, ${position.lineNumber}, ${position.column});")
  }

  fun insert(text: CharSequence, lineNumber: Int, column: Int = 1) {
    insert(text, Position(lineNumber, column))
  }

  fun dispatchKey(key: String) {
    if (key == "\\") {
      loadJs("simulateKeyPress(`\\\\`);")
      return
    }
    loadJs("simulateKeyPress(`$key`);")
  }
}
