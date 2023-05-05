package io.github.rosemoe.sora.langs.textmate;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.raredev.vcspace.util.PreferencesUtils;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.util.MyCharacter;
import io.github.rosemoe.sora.widget.SymbolPairMatch;
import java.util.List;
import org.eclipse.tm4e.core.grammar.IGrammar;
import org.eclipse.tm4e.languageconfiguration.model.AutoClosingPairConditional;
import org.eclipse.tm4e.languageconfiguration.model.LanguageConfiguration;

public class VCSpaceTMLanguage extends TextMateLanguage {

  protected VCSpaceTMLanguage(
      IGrammar grammar,
      LanguageConfiguration languageConfiguration,
      ThemeRegistry themeRegistry,
      boolean createIdentifiers) {
    super(grammar, languageConfiguration, null, themeRegistry, false);
  }

  public static VCSpaceTMLanguage create(String languageScopeName) {
    final GrammarRegistry grammarRegistry = GrammarRegistry.getInstance();
    var grammar = grammarRegistry.findGrammar(languageScopeName);

    if (grammar == null) {
      throw new IllegalArgumentException(
          String.format("Language with %s scope name not found", grammarRegistry));
    }

    var languageConfiguration = grammarRegistry.findLanguageConfiguration(grammar.getScopeName());

    return new VCSpaceTMLanguage(grammar, languageConfiguration, ThemeRegistry.getInstance(), true);
  }

  @Override
  public void requireAutoComplete(
      @NonNull ContentReference content,
      @NonNull CharPosition position,
      @NonNull CompletionPublisher publisher,
      @NonNull Bundle extraArguments) {}

  @Override
  public int getTabSize() {
    return PreferencesUtils.getEditorTABSize();
  }

  @Override
  public boolean useTab() {
    return !PreferencesUtils.useSpaces();
  }

  @Override
  public SymbolPairMatch getSymbolPairs() {
    SymbolPairMatch symbolPair = new SymbolPairMatch();
    if (languageConfiguration == null) {
      return symbolPair;
    }

    List<AutoClosingPairConditional> autoClosingPairs = languageConfiguration.getAutoClosingPairs();
    if (autoClosingPairs == null) {
      return symbolPair;
    }
    for (AutoClosingPairConditional autoClosingPair : autoClosingPairs) {
      symbolPair.putPair(
          autoClosingPair.open,
          new SymbolPairMatch.SymbolPair(
              autoClosingPair.open,
              autoClosingPair.close,
              new TextMateSymbolPairMatch.SymbolPairEx(autoClosingPair)));
    }
    return symbolPair;
  }

  public LanguageConfiguration getLanguageConfiguration() {
    return this.languageConfiguration;
  }

  public void editorCommitText(CharSequence text) {
    //
  }

  private boolean checkIsCompletionChar(char c) {
    return MyCharacter.isJavaIdentifierPart(c);
  }
}
