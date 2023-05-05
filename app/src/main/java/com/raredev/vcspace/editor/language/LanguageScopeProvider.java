package com.raredev.vcspace.editor.language;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raredev.vcspace.models.DocumentModel;
import com.raredev.vcspace.util.FileUtil;
import com.raredev.vcspace.util.Utils;
import java.util.Map;

public class LanguageScopeProvider {
  private static Map<String, String> scopes;

  static {
    Gson gson = new Gson();
    var type = new TypeToken<Map<String, String>>() {}.getType();
    scopes =
        gson.fromJson(
            FileUtil.readAssetFile(Utils.getContext(), "textmate/language_scopes.json"), type);
  }

  public static String scopeForFileName(String fileName) {
    String fileExt = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length()).toLowerCase();
    
    return scopes.get(fileExt);
  }
}
