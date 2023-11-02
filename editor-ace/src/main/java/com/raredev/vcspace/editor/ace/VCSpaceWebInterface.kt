package com.raredev.vcspace.editor.ace

import android.webkit.JavascriptInterface
import android.widget.Toast
import com.raredev.vcspace.editor.AceCodeEditor
import com.raredev.vcspace.events.OnContentChangeEvent
import org.greenrobot.eventbus.EventBus

class VCSpaceWebInterface(private val editor: AceCodeEditor) {
  @set:JavascriptInterface var hasUndo = false
  @set:JavascriptInterface var hasRedo = false
  @set:JavascriptInterface var value = ""
  var range: Range? = null
  var cursorPosition: Point? = null

  @JavascriptInterface
  fun showToast(toast: String) = Toast.makeText(editor.context, toast, Toast.LENGTH_SHORT).show()

  @Suppress("UNUSED_PARAMETER")
  @JavascriptInterface
  fun onContentChange(initialValue: String, modifiedValue: String) {
    editor.isModified = true
    EventBus.getDefault().post(OnContentChangeEvent(editor.file))
  }

  @JavascriptInterface
  fun setRange(startRow: Int, startColumn: Int, endRow: Int, endColumn: Int) {
    range = Range(Point(startRow, startColumn), Point(endRow, endColumn))
  }

  @JavascriptInterface
  fun setCursorPosition(row: Int, column: Int) {
    this.cursorPosition = Point(row, column)
  }
}
