package com.raredev.vcspace.editor.ace;

import android.webkit.WebView;

@FunctionalInterface
public interface OnEditorLoadedListener {
  public void onLoaded(WebView view);
}
