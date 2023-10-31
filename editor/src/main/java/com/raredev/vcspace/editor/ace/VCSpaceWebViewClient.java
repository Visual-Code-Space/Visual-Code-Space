package com.raredev.vcspace.editor.ace;

import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import androidx.webkit.WebViewClientCompat;
import com.raredev.vcspace.editor.VCSpaceCodeEditor;

public class VCSpaceWebViewClient extends WebViewClientCompat {

  private VCSpaceCodeEditor editor;

  public VCSpaceWebViewClient(VCSpaceCodeEditor editor) {
    this.editor = editor;
  }

  @Override
  public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
    return false;
  }

  @Override
  public void onPageFinished(WebView view, String url) {
    editor.loadOptions();
    if (editor.onEditorLoadedListener != null) editor.onEditorLoadedListener.onLoaded(view);
  }
}
