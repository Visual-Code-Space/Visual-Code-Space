package com.raredev.vcspace.ui.language.lua;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.raredev.vcspace.util.PreferencesUtils;
import io.github.rosemoe.sora.lang.completion.CompletionHelper;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.util.MyCharacter;
import io.github.rosemoe.sora.widget.SymbolPairMatch;

public class LuaLanguage extends TextMateLanguage {
  
  public LuaLanguage() {
    super(
        GrammarRegistry.getInstance().findGrammar("source.lua"),
        GrammarRegistry.getInstance().findLanguageConfiguration("source.lua"),
        null,
        ThemeRegistry.getInstance(),
        true);
    loadSymbolPairs();
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
  }

  @Override
  public boolean useTab() {
    return !PreferencesUtils.useUseSpaces();
  }

  private void loadSymbolPairs() {
    SymbolPairMatch symbolPairs = getSymbolPairs();
    symbolPairs.putPair("(", new SymbolPairMatch.SymbolPair("(", ")"));
    symbolPairs.putPair("{", new SymbolPairMatch.SymbolPair("{", "}"));
    symbolPairs.putPair("[", new SymbolPairMatch.SymbolPair("[", "]"));
    symbolPairs.putPair("\"", new SymbolPairMatch.SymbolPair("\"", "\""));
    symbolPairs.putPair("'", new SymbolPairMatch.SymbolPair("'", "'"));
  }
}
