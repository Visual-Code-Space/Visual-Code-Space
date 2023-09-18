package io.github.rosemoe.sora.langs.textmate.provider;

import com.raredev.vcspace.editor.IDECodeEditor;
import com.raredev.vcspace.editor.language.html.HtmlLanguage;
import com.raredev.vcspace.editor.language.java.JavaLanguage;
import com.raredev.vcspace.editor.language.json.JsonLanguage;
import com.raredev.vcspace.editor.language.kotlin.KotlinLanguage;
import com.raredev.vcspace.editor.language.lua.LuaLanguage;
import io.github.rosemoe.sora.langs.textmate.VCSpaceTMLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel;
import org.eclipse.tm4e.core.registry.IThemeSource;

public class TextMateProvider {

  public static void registerLanguages() throws Exception {
    GrammarRegistry.getInstance().loadGrammars("editor/textmate/languages.json");
    LanguageScopeProvider.init();

    var themeRegistry = ThemeRegistry.getInstance();
    var names = new String[] {"darcula", "abyss", "quietlight", "solarized_drak"};
    for (String name : names) {
      var path = "editor/scheme/" + name + ".json";
      themeRegistry
          .loadTheme(
              new ThemeModel(
                  IThemeSource.fromInputStream(
                      FileProviderRegistry.getInstance().tryGetInputStream(path), path, null),
                  name));
    }
  }

  public static VCSpaceTMLanguage createLanguage(IDECodeEditor editor, String fileName) {
    try {
      String scope = LanguageScopeProvider.scopeForFileName(fileName);

      switch (scope) {
        case "source.java":
          return new JavaLanguage(editor);
        case "source.kotlin":
          return new KotlinLanguage(editor);
        case "text.html.basic":
          return new HtmlLanguage(editor);
        case "source.lua":
          return new LuaLanguage(editor);
        case "source.json":
          return new JsonLanguage(editor);
      }

      return VCSpaceTMLanguage.create(scope);
    } catch (Exception e) {
      return null;
    }
  }
}
