package com.raredev.vcspace.editor.language.java;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.raredev.vcspace.editor.IDECodeEditor;
import com.raredev.vcspace.editor.completion.CompletionParams;
import com.raredev.vcspace.editor.completion.CompletionProvider;
import com.raredev.vcspace.editor.completion.VCSpaceCompletionItem;
import com.raredev.vcspace.editor.language.java.completion.JavaCompletionProvider;
import io.github.rosemoe.sora.lang.QuickQuoteHandler;
import io.github.rosemoe.sora.lang.QuickQuoteHandler.HandleResult;
import io.github.rosemoe.sora.lang.completion.CompletionHelper;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.lang.styling.Styles;
import io.github.rosemoe.sora.lang.styling.StylesUtils;
import io.github.rosemoe.sora.langs.textmate.VCSpaceTMLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.text.TextRange;
import io.github.rosemoe.sora.util.MyCharacter;
import java.util.List;

public class JavaLanguage extends VCSpaceTMLanguage {

  private IDECodeEditor editor;

  public JavaLanguage(IDECodeEditor editor) {
    super(
        GrammarRegistry.getInstance().findGrammar("source.java"),
        GrammarRegistry.getInstance().findLanguageConfiguration("source.java"),
        ThemeRegistry.getInstance(),
        "source.java");
    this.editor = editor;
    setCompleterKeywords(JavaCompletionProvider.javaKeywords);
  }

  @Override
  public void requireAutoComplete(
      @NonNull ContentReference content,
      @NonNull CharPosition position,
      @NonNull CompletionPublisher publisher,
      @NonNull Bundle extraArguments) {
    super.requireAutoComplete(content, position, publisher, extraArguments);
    var prefix =
        CompletionHelper.computePrefix(content, position, MyCharacter::isJavaIdentifierPart);
    
    CompletionParams params =
        new CompletionParams(
            editor,
            editor.getText().toString(),
            prefix,
            position.line,
            position.column,
            position.index);

    List<VCSpaceCompletionItem> completions =
        CompletionProvider.getCompletionProvider(JavaCompletionProvider.class)
            .getCompletions(params);
    for (VCSpaceCompletionItem item : completions) {
      publisher.addItem(item);
    }
  }

  @Override
  public QuickQuoteHandler getQuickQuoteHandler() {
    return new JavaQuoteHandler();
  }

  class JavaQuoteHandler implements QuickQuoteHandler {
    @NonNull
    @Override
    public HandleResult onHandleTyping(
        @NonNull String candidateCharacter,
        @NonNull Content text,
        @NonNull TextRange cursor,
        @Nullable Styles style) {
      if (!StylesUtils.checkNoCompletion(style, cursor.getStart())
          && !StylesUtils.checkNoCompletion(style, cursor.getEnd())
          && "\"".equals(candidateCharacter)
          && cursor.getStart().line == cursor.getEnd().line) {
        text.insert(cursor.getStart().line, cursor.getStart().column, "\"");
        text.insert(cursor.getEnd().line, cursor.getEnd().column + 1, "\"");
        return new HandleResult(
            true,
            new TextRange(
                text.getIndexer().getCharPosition(cursor.getStartIndex() + 1),
                text.getIndexer().getCharPosition(cursor.getEndIndex() + 1)));
      }
      return HandleResult.NOT_CONSUMED;
    }
  }
}
