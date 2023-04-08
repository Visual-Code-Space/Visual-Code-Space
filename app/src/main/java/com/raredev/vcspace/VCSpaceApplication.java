package com.raredev.vcspace;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import androidx.appcompat.app.AppCompatDelegate;
import com.raredev.vcspace.fragments.SettingsFragment;
import com.raredev.vcspace.util.ILogger;
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;
import org.eclipse.tm4e.core.registry.IThemeSource;

public class VCSpaceApplication extends Application {
  public static Context appContext;
  private ShutdownReceiver shutdownReceiver;

  @Override
  public void onCreate() {
    super.onCreate();
    registerShutdownReceiver();
    appContext = this;
    Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
    loadTextMate();
  }

  @Override
  public void onTerminate() {
    unregisterShutdownReceiver();
    super.onTerminate();
  }

  private void loadTextMate() {
    // Load editor themes
    try {
      FileProviderRegistry.getInstance().addFileProvider(new AssetsFileResolver(getAssets()));
      String[] themes = new String[] {"vcspace_dark", "vcspace_light"};
      ThemeRegistry themeRegistry = ThemeRegistry.getInstance();
      for (String name : themes) {
        String path = "textmate/" + name + ".json";
        themeRegistry.loadTheme(
            new ThemeModel(
                IThemeSource.fromInputStream(
                    FileProviderRegistry.getInstance().tryGetInputStream(path), path, null),
                name));
      }
      ILogger.info("ThemeLoader", "Themes loaded successfully");
    } catch (Exception e) {
      ILogger.error(
          "ThemeLoader", "Error when trying to load themes: \t" + Log.getStackTraceString(e));
    }
    // Load editor languages
    try {
      GrammarRegistry.getInstance().loadGrammars("textmate/languages.json");
      ILogger.info("LanguageLoader", "Languages loaded successfully");
    } catch (Exception e) {
      ILogger.error(
          "LanguageLoader", "Error when trying to load languages: \t" + Log.getStackTraceString(e));
    }
  }

  private void registerShutdownReceiver() {
    shutdownReceiver = new ShutdownReceiver();
    IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SHUTDOWN);
    registerReceiver(shutdownReceiver, intentFilter);
  }

  private void unregisterShutdownReceiver() {
    unregisterReceiver(shutdownReceiver);
  }

  private static class ShutdownReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      ILogger.clear();
    }
  }
}
