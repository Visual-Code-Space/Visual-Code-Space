package com.raredev.vcspace.ui.editor.textmate;

import androidx.annotation.NonNull;
import com.raredev.vcspace.ui.editor.formatter.VCSpaceFormatter;
import io.github.rosemoe.sora.lang.format.Formatter;
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.widget.SymbolPairMatch;
import org.eclipse.tm4e.core.grammar.IGrammar;
import org.eclipse.tm4e.languageconfiguration.model.LanguageConfiguration;

public class VCSpaceTextMateLanguage extends TextMateLanguage {
  private Formatter formatter;

  protected VCSpaceTextMateLanguage(
      IGrammar grammar,
      LanguageConfiguration languageConfiguration,
      GrammarRegistry grammarRegistry,
      ThemeRegistry themeRegistry,
      boolean createIdentifiers,
      String fileExtension) {
    super(grammar, languageConfiguration, grammarRegistry, themeRegistry, createIdentifiers);

    formatter = new VCSpaceFormatter(fileExtension);
    getSymbolPairsFor(fileExtension);
    useTab(false);
  }

  public static VCSpaceTextMateLanguage create(String languageScopeName, String fileExtension) {
    var grammarRegistry = GrammarRegistry.getInstance();
    var grammar = grammarRegistry.findGrammar(languageScopeName);

    if (grammar == null) {
      throw new IllegalArgumentException(
          String.format("Language with %s scope name not found", grammarRegistry));
    }

    var languageConfiguration = grammarRegistry.findLanguageConfiguration(grammar.getScopeName());

    return new VCSpaceTextMateLanguage(
        grammar,
        languageConfiguration,
        grammarRegistry,
        ThemeRegistry.getInstance(),
        true,
        fileExtension);
  }

  @Override
  @NonNull
  public Formatter getFormatter() {
    return formatter;
  }

  private void getSymbolPairsFor(String fileExtension) {
    SymbolPairMatch symbolPairs = getSymbolPairs();
    switch (fileExtension) {
      case "html":
        symbolPairs.putPair("<", new SymbolPairMatch.SymbolPair("<", ">"));
      case "kt":
      case "java":
      case "json":
        symbolPairs.putPair("(", new SymbolPairMatch.SymbolPair("(", ")"));
        symbolPairs.putPair("{", new SymbolPairMatch.SymbolPair("{", "}"));
        symbolPairs.putPair("[", new SymbolPairMatch.SymbolPair("[", "]"));
        symbolPairs.putPair("\"", new SymbolPairMatch.SymbolPair("\"", "\""));
        symbolPairs.putPair("'", new SymbolPairMatch.SymbolPair("'", "'"));
        break;
    }
  }
}
