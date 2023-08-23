package com.raredev.vcspace.util;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.raredev.vcspace.models.DocumentModel;
import com.raredev.vcspace.models.FileModel;
import com.raredev.vcspace.ui.panels.Panel;
import com.raredev.vcspace.ui.panels.editor.EditorPanel;
import com.raredev.vcspace.ui.panels.editor.SearcherPanel;
import com.raredev.vcspace.ui.panels.editor.WelcomePanel;
import com.raredev.vcspace.ui.panels.file.FileExplorerPanel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PanelUtils {

  public static String panelsToJson(List<Panel> panels) {
    if (panels.isEmpty()) return "";
    List<LinkedTreeMap<String, String>> panelsMapList = new ArrayList<>();
    Gson gson = new Gson();
    for (Panel panel : panels) {
      LinkedTreeMap<String, String> map = new LinkedTreeMap<>();
      map.put("type", panel.getClass().getSimpleName());
      map.put("pinned", String.valueOf(panel.isPinned()));

      if (panel instanceof EditorPanel) {
        map.put("document", gson.toJson(((EditorPanel) panel).getDocument()));
      }

      if (panel instanceof FileExplorerPanel) {
        map.put("currentPath", ((FileExplorerPanel) panel).getCurrentDir().getPath());
      }
      panelsMapList.add(map);
    }
    return gson.toJson(panelsMapList);
  }

  public static List<Panel> jsonToPanels(Context context, String json) {
    Gson gson = new Gson();

    var typeToken = new TypeToken<List<LinkedTreeMap<String, String>>>() {}.getType();

    List<LinkedTreeMap<String, String>> panelsMapList = gson.fromJson(json, typeToken);

    List<Panel> panels = new ArrayList<>();
    if (panelsMapList != null) {
      for (LinkedTreeMap<String, String> panelMap : panelsMapList) {
        String type = (String) panelMap.get("type");
        Panel panel = null;
        if (type.equals("EditorPanel")) {
          DocumentModel document =
              gson.fromJson((String) panelMap.get("document"), DocumentModel.class);
          panel = new EditorPanel(context, document);
        } else {
          panel = createPanel(context, type);
        }

        if (type.equals("FileExplorerPanel")) {
          ((FileExplorerPanel) panel)
              .setCurrentDir(
                  FileModel.fileToFileModel(new File((String) panelMap.get("currentPath"))));
        }
        panel.setPinned(Boolean.parseBoolean((String) panelMap.get("pinned")));
        panels.add(panel);
      }
    }
    return panels;
  }

  private static Panel createPanel(Context context, String type) {
    switch (type) {
      case "SearcherPanel":
        return new SearcherPanel(context);
      case "WelcomePanel":
        return new WelcomePanel(context);
      case "FileExplorerPanel":
        return new FileExplorerPanel(context);
      default:
        return null;
    }
  }
}
