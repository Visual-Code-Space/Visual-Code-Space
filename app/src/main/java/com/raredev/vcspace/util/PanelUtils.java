package com.raredev.vcspace.util;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.raredev.vcspace.models.DocumentModel;
import com.raredev.vcspace.models.FileModel;
import com.raredev.vcspace.ui.panels.Panel;
import com.raredev.vcspace.ui.panels.compiler.WebViewPanel;
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

      if (panel instanceof EditorPanel editorPanel) {
        map.put("document", gson.toJson(editorPanel.getDocument()));
      }

      if (panel instanceof FileExplorerPanel fileExplorer) {
        map.put("currentPath", fileExplorer.getCurrentDir().getPath());
      }
      
      if (panel instanceof WebViewPanel webViewPanel) {
        map.put("filePath", webViewPanel.getFilePath());
        map.put("supportZoom", String.valueOf(webViewPanel.isSupportZoom()));
        map.put("desktopMode", String.valueOf(webViewPanel.isDesktopMode()));
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
        String type = panelMap.get("type");
        Panel panel = null;
        if (type.equals("EditorPanel")) {
          DocumentModel document =
              gson.fromJson(panelMap.get("document"), DocumentModel.class);
          panel = new EditorPanel(context, document);
        } else {
          panel = createPanel(context, type);
        }

        if (type.equals("FileExplorerPanel")) {
          ((FileExplorerPanel) panel)
              .setCurrentDir(
                  FileModel.fileToFileModel(new File(panelMap.get("currentPath"))));
        }
        
        if (type.equals("WebViewPanel")) {
          var webViewPanel = (WebViewPanel) panel;
          webViewPanel.loadFile(panelMap.get("filePath"));
          webViewPanel.setSupportZoom(Boolean.parseBoolean(panelMap.get("supportZoom")));
          webViewPanel.setDesktopMode(Boolean.parseBoolean(panelMap.get("desktopMode")));
        }
        panel.setPinned(Boolean.parseBoolean(panelMap.get("pinned")));
        panels.add(panel);
      }
    }
    return panels;
  }
  
  public static String getUniqueTabTitle(EditorPanel selectedPanel, List<Panel> panels) {
    int count = 0;
    UniqueNameBuilder<EditorPanel> builder = new UniqueNameBuilder<>("", "/");

    for (Panel panel : panels) {
      if (panel instanceof EditorPanel) {
        var doc = ((EditorPanel) panel).getDocument();
        if (doc.getName().equals(selectedPanel.getDocument().getName())) {
          count++;
        }
        builder.addPath((EditorPanel) panel, doc.getPath());
      }
    }
    if (count > 1) {
      return builder.getShortPath(selectedPanel);
    } else {
      return selectedPanel.getDocument().getName();
    }
  }

  private static Panel createPanel(Context context, String type) {
    switch (type) {
      case "SearcherPanel":
        return new SearcherPanel(context);
      case "WelcomePanel":
        return new WelcomePanel(context);
      case "FileExplorerPanel":
        return new FileExplorerPanel(context);
      case "WebViewPanel":
        return new WebViewPanel(context);
      default:
        return null;
    }
  }
}