package com.vcspace.plugin;

import org.pf4j.CompoundPluginDescriptorFinder;
import org.pf4j.JarPluginManager;
import org.pf4j.ManifestPluginDescriptorFinder;
import org.pf4j.PluginLoader;
import org.pf4j.PluginManager;

public class VCSpacePluginManager {
  private final PluginManager pluginManager;

  public VCSpacePluginManager() {
    // System.setProperty("pf4j.pluginsDir", "./src");
    this.pluginManager =
        new JarPluginManager() {

          @Override
          protected CompoundPluginDescriptorFinder createPluginDescriptorFinder() {
            return new CompoundPluginDescriptorFinder().add(new ManifestPluginDescriptorFinder());
          }
        };
    pluginManager.loadPlugins();
    pluginManager.startPlugins();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> pluginManager.stopPlugins()));
  }

  public void applyAllThemes() {
    pluginManager
        .getExtensions(ThemePlugin.class)
        .forEach(
            plugin -> {
              if (plugin != null) {
                plugin.applyTheme();
              }
            });
  }
}
