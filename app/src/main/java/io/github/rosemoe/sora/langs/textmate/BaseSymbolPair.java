package io.github.rosemoe.sora.langs.textmate;

import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.widget.SymbolPairMatch;

public class BaseSymbolPair extends SymbolPairMatch {

  private final SymbolPair.SymbolPairEx isSelected =
      new SymbolPair.SymbolPairEx() {
        @Override
        public boolean shouldDoAutoSurround(Content content) {
          return content.getCursor().isSelected();
        }
      };

  public BaseSymbolPair() {
    super.putPair('{', new SymbolPair("{", "}"));
    super.putPair('(', new SymbolPair("(", ")"));
    super.putPair('[', new SymbolPair("[", "]"));
    super.putPair('"', new SymbolPair("\"", "\"", isSelected));
    super.putPair('\'', new SymbolPair("'", "'", isSelected));
  }
}
