package com.raredev.vcspace.editor.completion;

import android.graphics.drawable.Drawable;
import io.github.rosemoe.sora.lang.completion.CompletionItem;

public abstract class VCSpaceCompletionItem extends CompletionItem {
  public CharSequence type;

  public VCSpaceCompletionItem(
      CharSequence label, CharSequence desc, CharSequence type, Drawable icon) {
    super(label, desc, icon);
    this.type = type;
  }
}
