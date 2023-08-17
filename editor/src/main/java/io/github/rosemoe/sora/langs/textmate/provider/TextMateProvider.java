package io.github.rosemoe.sora.langs.textmate.provider;

import com.raredev.vcspace.editor.IDECodeEditor;
import com.raredev.vcspace.editor.language.html.HtmlLanguage;
import com.raredev.vcspace.editor.language.java.JavaLanguage;
import com.raredev.vcspace.editor.language.kotlin.KotlinLanguage;
import com.raredev.vcspace.editor.language.lua.LuaLanguage;
import com.raredev.vcspace.util.Utils;
import io.github.rosemoe.sora.langs.textmate.VCSpaceTMLanguage;
import io.github.rosemoe.sora.langs.textmate.VCSpaceTMScheme;
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;
import org.eclipse.tm4e.core.registry.IThemeSource;

public class TextMateProvider {

  private static VCSpaceTMScheme lightScheme;
  private static VCSpaceTMScheme darkScheme;

  public static void registerLanguages() {
    GrammarRegistry.getInstance().loadGrammars("textmate/languages.json");
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
      }

      return VCSpaceTMLanguage.create(scope);
    } catch (Exception e) {
      return null;
    }
  }

  public static EditorColorScheme getColorScheme() {
    try {
      if (Utils.isDarkMode()) {
        return getSchemeDark();
      }
      return getSchemeLight();
    } catch (Exception e) {
      return new EditorColorScheme();
    }
  }

  public static VCSpaceTMScheme getSchemeLight() throws Exception {
    if (lightScheme == null) {
      ThemeModel themeModel =
          new ThemeModel(
              IThemeSource.fromInputStream(
                  FileProviderRegistry.getInstance().tryGetInputStream("textmate/quietlight.json"),
                  "textmate/quietlight.json",
                  null),
              "quietlight");
      lightScheme = new VCSpaceTMScheme(themeModel);
    }
    return lightScheme;
  }

  public static VCSpaceTMScheme getSchemeDark() throws Exception {
    if (darkScheme == null) {
      ThemeModel themeModel =
          new ThemeModel(
              IThemeSource.fromInputStream(
                  FileProviderRegistry.getInstance().tryGetInputStream("textmate/darcula.json"),
                  "textmate/darcula.json",
                  null),
              "darcula");
      darkScheme = new VCSpaceTMScheme(themeModel);
    }
    return darkScheme;
  }
}
