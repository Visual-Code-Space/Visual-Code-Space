package io.github.rosemoe.sora.langs.textmate.provider;

import com.blankj.utilcode.util.FileUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raredev.vcspace.BaseApp;
import com.raredev.vcspace.util.FileUtil;
import com.raredev.vcspace.util.Utils;
import java.util.Map;

public class LanguageScopeProvider {
  private static Map<String, String> scopes;

  static {
    var type = new TypeToken<Map<String, String>>() {}.getType();
    scopes =
        new Gson()
            .fromJson(
                FileUtil.readAssetFile(
                    BaseApp.getInstance(), "editor/textmate/language_scopes.json"),
                type);
  }

  public static String scopeForFileName(String fileName) {
    return scopes.get(FileUtils.getFileExtension(fileName).toLowerCase());
  }
}
