package com.raredev.vcspace.editor.ace.callback.event;

@FunctionalInterface
public interface OnContentChangeEventCallback {
  public void onContentChange(String initialValue, String modifiedValue);
}
