package com.raredev.vcspace.editor.ace;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;
import com.raredev.vcspace.editor.VCSpaceCodeEditor;

public class VCSpaceWebInterface {
  private VCSpaceCodeEditor editor;

  public static boolean hasUndo;
  public static boolean hasRedo;
  public static String value;

  /** Instantiate the interface and set the context. */
  public VCSpaceWebInterface(VCSpaceCodeEditor c) {
    editor = c;
  }

  /** Show a toast from the web page. */
  @JavascriptInterface
  public void showToast(String toast) {
    Toast.makeText(editor.getContext(), toast, Toast.LENGTH_SHORT).show();
  }

  @JavascriptInterface
  public void onContentChange(String initialValue, String modifiedValue) {
    if (editor.getOnContentChangeEventCallback() != null) {
      editor.getOnContentChangeEventCallback().onContentChange(initialValue, modifiedValue);
    }
  }

  @JavascriptInterface
  public void setHasUndo(boolean hasUndo) {
    VCSpaceWebInterface.hasUndo = hasUndo;
  }

  @JavascriptInterface
  public void setHasRedo(boolean hasRedo) {
    VCSpaceWebInterface.hasRedo = hasRedo;
  }

  @JavascriptInterface
  public void setValue(String value) {
    VCSpaceWebInterface.value = value;
  }
}
