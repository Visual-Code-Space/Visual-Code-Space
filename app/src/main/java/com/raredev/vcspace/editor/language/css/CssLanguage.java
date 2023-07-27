package com.raredev.vcspace.editor.language.css;

import com.raredev.vcspace.editor.IDECodeEditor;
import io.github.rosemoe.sora.langs.textmate.VCSpaceTMLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;

public class CssLanguage extends VCSpaceTMLanguage {

  private IDECodeEditor editor;

  public CssLanguage(IDECodeEditor editor) {
    super(
        GrammarRegistry.getInstance().findGrammar("source.css"),
        GrammarRegistry.getInstance().findLanguageConfiguration("source.css"),
        ThemeRegistry.getInstance(),
        "source.css");
    this.editor = editor;
  }
}
