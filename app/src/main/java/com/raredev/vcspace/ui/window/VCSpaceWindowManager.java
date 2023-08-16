package com.raredev.vcspace.ui.window;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;

public class VCSpaceWindowManager {
  public static final String SEARCHER_WINDOW = "searcher";
  public static final String WEBVIEW_WINDOW = "webview";

  private static VCSpaceWindowManager instance;

  public static VCSpaceWindowManager getInstance(Context context) {
    if (instance == null) {
      instance = new VCSpaceWindowManager(context);
    }
    return instance;
  }

  private Map<String, VCSpaceWindow> windows = new HashMap<>();

  public VCSpaceWindowManager(Context context) {
    windows.put(SEARCHER_WINDOW, new SearcherWindow(context));
    windows.put(WEBVIEW_WINDOW, new WebViewWindow(context));
  }

  public VCSpaceWindow getWindow(String key) {
    return windows.get(key);
  }
  
  public Map<String, VCSpaceWindow> getWindows() {
    return windows;
  }
}
