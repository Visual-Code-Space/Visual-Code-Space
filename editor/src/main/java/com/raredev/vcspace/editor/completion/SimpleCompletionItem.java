package com.raredev.vcspace.editor.completion;

import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import io.github.rosemoe.sora.lang.completion.CompletionItemKind;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.widget.CodeEditor;

public class SimpleCompletionItem extends VCSpaceCompletionItem {

  public String commitText;

  public SimpleCompletionItem(int prefixLength, String commitText) {
    this(commitText, prefixLength, commitText);
  }

  public SimpleCompletionItem(CharSequence label, int prefixLength, String commitText) {
    this(label, null, prefixLength, commitText);
  }

  public SimpleCompletionItem(
      CharSequence label, CharSequence desc, int prefixLength, String commitText) {
    this(label, desc, null, null, prefixLength, commitText);
  }

  public SimpleCompletionItem(
      CharSequence label,
      CharSequence desc,
      CharSequence type,
      int prefixLength,
      String commitText) {
    this(label, desc, type, null, prefixLength, commitText);
  }

  public SimpleCompletionItem(
      CharSequence label,
      CharSequence desc,
      CharSequence type,
      Drawable icon,
      int prefixLength,
      String commitText) {
    super(
        label,
        desc,
        type,
        icon == null ? SimpleCompletionIconDrawer.draw(SimpleCompletionItemKind.UNKNOWN) : icon);
    this.commitText = commitText;
    this.prefixLength = prefixLength;
  }

  @Override
  public SimpleCompletionItem desc(CharSequence desc) {
    super.desc(desc);
    return this;
  }

  @Override
  public SimpleCompletionItem icon(Drawable icon) {
    super.icon(icon);
    return this;
  }

  @Override
  public SimpleCompletionItem label(CharSequence label) {
    super.label(label);
    return this;
  }

  @Override
  public SimpleCompletionItem kind(CompletionItemKind kind) {
    super.kind(kind);
    return this;
  }

  public SimpleCompletionItem commit(int prefixLength, String commitText) {
    this.prefixLength = prefixLength;
    this.commitText = commitText;
    return this;
  }

  @Override
  public void performCompletion(
      @NonNull CodeEditor editor, @NonNull Content text, int line, int column) {
    if (commitText == null) {
      return;
    }
    if (prefixLength == 0) {
      text.insert(line, column, commitText);
      return;
    }
    text.replace(line, column - prefixLength, line, column, commitText);
  }
}
