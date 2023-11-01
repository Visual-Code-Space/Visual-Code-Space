package com.raredev.vcspace.editor.ace;

import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;

public class VCSpaceWebChromeClient extends WebChromeClient {

  @Override
  public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
    Log.d(
        "VCSpace",
        consoleMessage.message()
            + " -- From line "
            + consoleMessage.lineNumber()
            + " of "
            + consoleMessage.sourceId());
    return true;
  }
}
