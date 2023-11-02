package com.raredev.vcspace.editor.ace

import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient

class VCSpaceWebChromeClient : WebChromeClient() {
  override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
    Log.d(
        "VCSpace",
        "${consoleMessage.message()} -- From line ${consoleMessage.lineNumber()} of ${consoleMessage.sourceId()}")
    return true
  }
}
