package com.raredev.vcspace.plugin;

import com.blankj.utilcode.util.GsonUtils;
import com.raredev.vcspace.util.FileUtil;
import com.raredev.vcspace.util.ILogger;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PluginsLoader {

  public static final Map<String, Plugin> plugins = new HashMap<>();

  public static void loadPlugins() {
    String pluginsPath = "/storage/emulated/0/VCSpace/plugins/";

    File pluginsFolder = new File(pluginsPath);

    if (!pluginsFolder.exists()) {
      pluginsFolder.mkdirs();
    }
    File[] pluginsList = pluginsFolder.listFiles();

    for (File file : pluginsList) {
      if (file.isDirectory()) {
        loadPlugin(file);
      }
    }
  }

  private static void loadPlugin(File file) {
    File pluginFile = new File(file.getAbsolutePath() + "/plugin.json");
    if (pluginFile.exists()) {
      Plugin plugin = GsonUtils.fromJson(FileUtil.readFile(pluginFile), Plugin.class);

      plugins.put(plugin.getName(), plugin);

      ILogger.debug("PluginRegistry", "Plugin: " + plugin.getName() + " loaded!");
    }
  }
}
