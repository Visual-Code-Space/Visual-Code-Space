package com.raredev.vcspace.editor.language.kotlin;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.raredev.vcspace.editor.IDECodeEditor;
import com.raredev.vcspace.editor.completion.CompletionParams;
import com.raredev.vcspace.editor.completion.CompletionProvider;
import com.raredev.vcspace.editor.completion.VCSpaceCompletionItem;
import com.raredev.vcspace.editor.language.kotlin.completion.KotlinCompletionProvider;
import io.github.rosemoe.sora.lang.completion.CompletionHelper;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.langs.textmate.VCSpaceTMLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.util.MyCharacter;
import java.util.List;

public class KotlinLanguage extends VCSpaceTMLanguage {
  private IDECodeEditor editor;

  public KotlinLanguage(IDECodeEditor editor) {
    super(
        GrammarRegistry.getInstance().findGrammar("source.kotlin"),
        GrammarRegistry.getInstance().findLanguageConfiguration("source.kotlin"),
        ThemeRegistry.getInstance(),
        false);
    this.editor = editor;
    setCompleterKeywords(KotlinCompletionProvider.kotlinKeywords);
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
        CompletionProvider.getCompletionProvider(KotlinCompletionProvider.class)
            .getCompletions(params);
    for (VCSpaceCompletionItem item : completions) {
      publisher.addItem(item);
    }
  }
}
