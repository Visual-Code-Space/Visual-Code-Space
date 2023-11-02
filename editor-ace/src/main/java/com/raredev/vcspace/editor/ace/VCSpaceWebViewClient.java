package com.raredev.vcspace.editor.ace;

import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import androidx.webkit.WebViewClientCompat;
import com.raredev.vcspace.editor.AceCodeEditor;

public class VCSpaceWebViewClient extends WebViewClientCompat {

  private AceCodeEditor editor;

  public VCSpaceWebViewClient(AceCodeEditor editor) {
    this.editor = editor;
  }

  @Override
  public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
    return false;
  }

  @Override
  public void onPageFinished(WebView view, String url) {
    editor.loadOptions();
    if (editor.getOnEditorLoadedListener() != null) editor.getOnEditorLoadedListener().onLoaded(view);
  }
}
