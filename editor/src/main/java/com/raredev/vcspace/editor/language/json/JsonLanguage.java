package com.raredev.vcspace.editor.language.json;

import com.blankj.utilcode.util.JsonUtils;
import com.raredev.vcspace.editor.IDECodeEditor;
import io.github.rosemoe.sora.langs.textmate.VCSpaceTMLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.TextRange;

public class JsonLanguage extends VCSpaceTMLanguage {

  private IDECodeEditor editor;

  public JsonLanguage(IDECodeEditor editor) {
    super(
        GrammarRegistry.getInstance().findGrammar("source.json"),
        GrammarRegistry.getInstance().findLanguageConfiguration("source.json"),
        ThemeRegistry.getInstance());
    this.editor = editor;
  }

  @Override
  public String formatCode(Content text, TextRange range) {
    return JsonUtils.formatJson(text.toString(), getTabSize());
  }
}
