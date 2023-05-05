package io.github.rosemoe.sora.langs.textmate;

import io.github.rosemoe.sora.lang.format.AsyncFormatter;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.TextRange;

public class TMFormatter extends AsyncFormatter {
  private VCSpaceTMLanguage language;

  public TMFormatter(VCSpaceTMLanguage language) {
    this.language = language;
  }

  @Override
  public TextRange formatAsync(Content text, TextRange range) {
    text.replace(0, text.toString().length(), language.formatCode(text, range));
    return range;
  }

  @Override
  public TextRange formatRegionAsync(Content text, TextRange range1, TextRange range2) {
    return range2;
  }
}
