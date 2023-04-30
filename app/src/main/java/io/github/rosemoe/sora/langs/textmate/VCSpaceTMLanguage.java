package io.github.rosemoe.sora.langs.textmate;

import com.raredev.vcspace.util.PreferencesUtils;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
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
    super(grammar, languageConfiguration, null, themeRegistry, createIdentifiers);
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

  public LanguageConfiguration getLanguageConfiguration() {
    if (languageConfiguration != null) {
      return this.languageConfiguration;
    }
    return null;
  }

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

    List<AutoClosingPairConditional> autoClosingPairs = languageConfiguration.getAutoClosingPairs();

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
}
