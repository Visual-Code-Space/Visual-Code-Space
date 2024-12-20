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

  var onEditorLoadCallbacks = mutableListOf<suspend CoroutineScope.(MonacoEditor) -> Unit>()
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
          ConsoleMessage.MessageLevel.ERROR -> Log.e(TAG, consoleMessage.message())
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

  fun addOnEditorLoadCallback(callback: suspend CoroutineScope.(MonacoEditor) -> Unit) {
    onEditorLoadCallbacks.add(callback)
  }

  private suspend fun loadJs(script: String, resultCallback: (String) -> Unit = {}) {
    withContext(Dispatchers.Unconfined) {
      evaluateJavascript(script, resultCallback)
    }
  }

  private suspend fun setEditorOptionInternal(option: String, value: Any) {
    loadJs("setEditorOptions(`$option`, `$value`);")
  }

  suspend fun <T : Option<Any>> setEditorOption(option: IEditorOption<T>) {
    setEditorOptionInternal(option.option.name, option.defaultValue.value)
  }

  suspend fun <T : Option<Any>> setEditorOption(option: EditorOption, value: T) {
    setEditorOption(IEditorOption(option, value))
  }

  suspend fun focusEditor() {
    loadJs("focusEditor();")
  }

  fun undo() = editorScope.launch { loadJs("undo();") }
  fun redo() = editorScope.launch { loadJs("redo();") }

  suspend fun setText(text: CharSequence) {
    webInterface.value = text.toString()
    loadJs("setText(`$text`);")
  }

  fun getText() = webInterface.value
  fun canUndo() = webInterface.canUndo
  fun canRedo() = webInterface.canRedo
  val isContentModified = webInterface.isModified

  fun getPosition() = Position(webInterface.lineNumber, webInterface.column)

  suspend fun setLanguage(language: MonacoLanguage) = loadJs("setLanguage(`${language.value}`);")

  suspend fun setFontSize(fontSize: Int) = setEditorOptionInternal("fontSize", fontSize)

  suspend fun setTheme(theme: MonacoTheme) = loadJs("setTheme(`${theme.value}`);")

  suspend fun setWordWrap(wordWrap: WordWrap) {
    setEditorOption(EditorOption.wordWrap, wordWrap)
  }

  suspend fun setWrappingStrategy(wrappingStrategy: WrappingStrategy) {
    setEditorOption(EditorOption.wrappingStrategy, wrappingStrategy)
  }

  suspend fun setWordBreak(wordBreak: WordBreak) {
    setEditorOption(EditorOption.wordBreak, wordBreak)
  }

  suspend fun setMatchBrackets(matchBrackets: MatchBrackets) {
    setEditorOption(EditorOption.matchBrackets, matchBrackets)
  }

  suspend fun setCursorStyle(cursorStyle: TextEditorCursorStyle) {
    loadJs("setCursorStyle(${cursorStyle.value});")
  }

  suspend fun setReadOnly(readOnly: Boolean) {
    setEditorOptionInternal("readOnly", readOnly)
  }

  suspend fun setMinimapOptions(minimapOptions: MinimapOptions) {
    loadJs("applyMinimapOptions(`${minimapOptions.toJson()}`);")
  }

  suspend fun setGlyphMargin(glyphMargin: Boolean) {
    setEditorOptionInternal("glyphMargin", glyphMargin)
  }

  suspend fun setFolding(folding: Boolean) = setEditorOptionInternal("folding", folding)

  suspend fun setInDiffEditor(inDiffEditor: Boolean) {
    setEditorOptionInternal("inDiffEditor", inDiffEditor)
  }

  suspend fun setLetterSpacing(letterSpacing: Number) {
    setEditorOptionInternal("letterSpacing", letterSpacing)
  }

  suspend fun setLineDecorationsWidth(lineDecorationsWidth: Number) {
    setEditorOptionInternal("lineDecorationsWidth", lineDecorationsWidth)
  }

  suspend fun setLineNumbersMinChars(lineNumbersMinChars: Number) {
    setEditorOptionInternal("lineNumbersMinChars", lineNumbersMinChars)
  }

  suspend fun setAcceptSuggestionOnCommitCharacter(acceptSuggestionOnCommitCharacter: Boolean) {
    setEditorOptionInternal("acceptSuggestionOnCommitCharacter", acceptSuggestionOnCommitCharacter)
  }

  suspend fun setAcceptSuggestionOnEnter(acceptSuggestionOnEnter: AcceptSuggestionOnEnter) {
    setEditorOptionInternal("acceptSuggestionOnEnter", acceptSuggestionOnEnter.value)
  }

  suspend fun setCursorBlinkingStyle(cursorBlinkingStyle: TextEditorCursorBlinkingStyle) {
    loadJs("setCursorBlinkingStyle(${cursorBlinkingStyle.value});")
  }

  suspend fun insert(text: CharSequence, position: Position) {
    loadJs("insert(`$text`, ${position.lineNumber}, ${position.column});")
  }

  suspend fun insert(text: CharSequence, lineNumber: Int, column: Int = 1) {
    insert(text, Position(lineNumber, column))
  }
}
