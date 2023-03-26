package com.raredev.vcspace.ui.language.html.completion;

import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import io.github.rosemoe.sora.lang.completion.CompletionItem;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.Cursor;
import io.github.rosemoe.sora.widget.CodeEditor;

public class HtmlCompletionItem extends CompletionItem {

  public String commitText;

  public HtmlCompletionItem(
      CharSequence label, CharSequence desc, int prefixLength, String commitText) {
    this(label, desc, null, prefixLength, commitText);
  }

  public HtmlCompletionItem(
      CharSequence label, CharSequence desc, Drawable icon, int prefixLength, String commitText) {
    super(label, desc, icon);
    this.commitText = commitText;
    this.prefixLength = prefixLength;
  }

  @Override
  public void performCompletion(
      @NonNull CodeEditor editor, @NonNull Content text, int line, int column) {
    if (commitText == null) {
      return;
    }

    String startTag = "<" + commitText + ">";
    String endTag = "</" + commitText + ">";

    commitText = startTag + endTag;

    int startTagIndex = commitText.indexOf(startTag);
    int endTagIndex = commitText.indexOf(endTag);
    int middleIndex = (startTagIndex + startTag.length() + endTagIndex) / 2;

    final Cursor cursor = text.getCursor();

    if (prefixLength == 0) {
      text.insert(line, column, commitText);
      cursor.set(line, column + middleIndex);
      return;
    }
    text.replace(line, column - prefixLength, line, column, commitText);
    cursor.set(line, (column - prefixLength) + middleIndex);
  }
}
