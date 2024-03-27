/*
 *    sora-editor - the awesome code editor for Android
 *    https://github.com/Rosemoe/sora-editor
 *    Copyright (C) 2020-2023  Rosemoe
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 *
 *     Please contact Rosemoe by email 2073412493@qq.com if you need
 *     additional information or have any questions
 */
package io.github.rosemoe.sora.langs.textmate;


import io.github.rosemoe.sora.lang.EmptyLanguage;
import io.github.rosemoe.sora.lang.analysis.AnalyzeManager;
import io.github.rosemoe.sora.lang.smartEnter.NewlineHandler;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.model.GrammarDefinition;
import io.github.rosemoe.sora.widget.SymbolPairMatch;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tm4e.core.grammar.IGrammar;
import org.eclipse.tm4e.languageconfiguration.model.LanguageConfiguration;
import java.util.Objects;

public class TextMateLanguage extends EmptyLanguage {

  private static final GrammarRegistry grammarRegistry = GrammarRegistry.getInstance();

  private int tabSize = 4;
  private boolean useTab = false;

  TextMateAnalyzer textMateAnalyzer;

  LanguageConfiguration languageConfiguration;

  final TextMateNewlineHandler[] newlineHandlers;

  final TextMateSymbolPairMatch symbolPairMatch;

  private TextMateNewlineHandler newlineHandler;

  protected TextMateLanguage(IGrammar grammar, LanguageConfiguration languageConfiguration) {
    this.symbolPairMatch = new TextMateSymbolPairMatch(this);
    this.newlineHandlers = new TextMateNewlineHandler[1];

    createAnalyzerAndNewlineHandler(grammar, languageConfiguration);
  }

  private void createAnalyzerAndNewlineHandler(
      IGrammar grammar, LanguageConfiguration languageConfiguration) {
    final var lastAnalyzer = this.textMateAnalyzer;
    if (lastAnalyzer != null) {
      lastAnalyzer.setReceiver(null);
      lastAnalyzer.destroy();
    }
    try {
      this.textMateAnalyzer = new TextMateAnalyzer(this, grammar);
    } catch (Exception e) {
      e.printStackTrace();
    }
    this.languageConfiguration = languageConfiguration;
    this.newlineHandlers[0] = this.newlineHandler = new TextMateNewlineHandler(this);
    if (languageConfiguration != null) {
      // because the editor will only get the symbol pair matcher once
      // (caching object to stop repeated new object created),
      // the symbol pair needs to be updated inside the symbol pair matcher.
      symbolPairMatch.updatePair();
    }
  }

  public void updateLanguage(String scopeName) {
    var grammar = grammarRegistry.findGrammar(scopeName);
    var languageConfiguration = grammarRegistry.findLanguageConfiguration(grammar.getScopeName());
    createAnalyzerAndNewlineHandler(grammar, languageConfiguration);
  }

  public void updateLanguage(GrammarDefinition grammarDefinition) {
    var grammar = grammarRegistry.loadGrammar(grammarDefinition);

    var languageConfiguration = grammarRegistry.findLanguageConfiguration(grammar.getScopeName());

    createAnalyzerAndNewlineHandler(grammar, languageConfiguration);
  }

  @NonNull
  @Override
  public AnalyzeManager getAnalyzeManager() {
    return Objects.requireNonNullElse(textMateAnalyzer, EmptyAnalyzeManager.INSTANCE);
  }

  /** Set tab size. The tab size is used to compute code blocks. */
  public void setTabSize(int tabSize) {
    this.tabSize = tabSize;
  }

  public int getTabSize() {
    return tabSize;
  }

  @Override
  public boolean useTab() {
    return useTab;
  }

  public void useTab(boolean useTab) {
    this.useTab = useTab;
  }

  public void setSymbolPairMatchEnabled(boolean enabled) {
    symbolPairMatch.setEnabled(enabled);
  }

  public TextMateNewlineHandler getNewlineHandler() {
    return newlineHandler;
  }

  @Override
  public SymbolPairMatch getSymbolPairs() {
    return symbolPairMatch;
  }

  @Override
  public NewlineHandler[] getNewlineHandlers() {
    return newlineHandlers;
  }

  @Nullable
  public LanguageConfiguration getLanguageConfiguration() {
    return languageConfiguration;
  }
}
