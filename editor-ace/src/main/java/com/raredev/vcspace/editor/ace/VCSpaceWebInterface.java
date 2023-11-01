package com.raredev.vcspace.editor.ace;

import android.webkit.JavascriptInterface;
import android.widget.Toast;
import com.raredev.vcspace.editor.AceCodeEditor;
import com.raredev.vcspace.events.OnContentChangeEvent;
import org.greenrobot.eventbus.EventBus;

public class VCSpaceWebInterface {
  private AceCodeEditor editor;

  public boolean hasUndo;
  public boolean hasRedo;
  public String value;

  /** Instantiate the interface and set the context. */
  public VCSpaceWebInterface(AceCodeEditor c) {
    editor = c;
  }

  /** Show a toast from the web page. */
  @JavascriptInterface
  public void showToast(String toast) {
    Toast.makeText(editor.getContext(), toast, Toast.LENGTH_SHORT).show();
  }

  @JavascriptInterface
  public void onContentChange(String initialValue, String modifiedValue) {
    editor.setModified(true);
    EventBus.getDefault().post(new OnContentChangeEvent(editor.getFile()));
  }

  @JavascriptInterface
  public void setHasUndo(boolean hasUndo) {
    this.hasUndo = hasUndo;
  }

  @JavascriptInterface
  public void setHasRedo(boolean hasRedo) {
    this.hasRedo = hasRedo;
  }

  @JavascriptInterface
  public void setValue(String value) {
    this.value = value;
  }
}
