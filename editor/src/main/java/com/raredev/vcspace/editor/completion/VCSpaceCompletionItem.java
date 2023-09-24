package com.raredev.vcspace.editor.completion;

import android.graphics.drawable.Drawable;
import io.github.rosemoe.sora.lang.completion.CompletionItem;

public abstract class VCSpaceCompletionItem extends CompletionItem {
  
  public CompletionItemKind completionKind;

  public VCSpaceCompletionItem(
      CharSequence label, CharSequence desc, CompletionItemKind completionKind) {
    super(label, desc, null);
    this.completionKind = completionKind;
  }
}
