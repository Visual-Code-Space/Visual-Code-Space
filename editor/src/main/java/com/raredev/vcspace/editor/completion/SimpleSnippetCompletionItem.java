package com.raredev.vcspace.editor.completion;

import androidx.annotation.NonNull;
import io.github.rosemoe.sora.lang.completion.SnippetDescription;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.widget.CodeEditor;

public class SimpleSnippetCompletionItem extends VCSpaceCompletionItem {

  private final SnippetDescription snippet;

  public SimpleSnippetCompletionItem(
      CharSequence label, CharSequence desc, SnippetDescription snippet) {
    this(label, desc, CompletionItemKind.SNIPPET, snippet);
  }
  
  public SimpleSnippetCompletionItem(
      CharSequence label, CharSequence desc, CompletionItemKind completionKind, SnippetDescription snippet) {
    super(label, desc, completionKind);
    this.snippet = snippet;
  }

  @Override
  public void performCompletion(
      @NonNull CodeEditor editor, @NonNull Content text, @NonNull CharPosition position) {
    int prefixLength = snippet.getSelectedLength();
    var selectedText = text.subSequence(position.index - prefixLength, position.index).toString();
    int actionIndex = position.index;
    if (snippet.getDeleteSelected()) {
      text.delete(position.index - prefixLength, position.index);
      actionIndex -= prefixLength;
    }
    editor.getSnippetController().startSnippet(actionIndex, snippet.getSnippet(), selectedText);
  }

  @Override
  public void performCompletion(
      @NonNull CodeEditor editor, @NonNull Content text, int line, int column) {
    // do nothing
  }
}
