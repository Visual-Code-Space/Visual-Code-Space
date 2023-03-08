package com.raredev.vcspace.tools;

import android.content.Context;
import com.raredev.common.util.FileUtil;
import com.raredev.vcspace.adapters.model.FileTemplateModel;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;

public class TemplatesParser {
  private static List<FileTemplateModel> templates = new ArrayList<>();

  public static List<FileTemplateModel> getTemplates() {
    return templates;
  }

  public static void loadTemplatesFromJson(Context context) {
    try {
      String templatesFile = context.getExternalFilesDir("template") + "/templates.json";
      if (!new File(templatesFile).exists()) {
        FileUtil.writeFile(templatesFile, FileUtil.readAssetFile(context, "default_templates.json"));
      }
      templates.clear();
      JSONObject jsonObj = new JSONObject(FileUtil.readFile(templatesFile));
      Iterator iterator = jsonObj.keys();
      while (iterator.hasNext()) {
        String fileExtension = (String) iterator.next();
        String templateCode = jsonObj.getString(fileExtension);

        if (fileExtension != null && templateCode != null) {
          templates.add(new FileTemplateModel(fileExtension, templateCode));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
