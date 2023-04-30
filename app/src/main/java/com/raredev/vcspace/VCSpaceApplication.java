package com.raredev.vcspace;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import com.google.android.material.color.DynamicColors;
import com.raredev.vcspace.extension.ExtensionAPI;
import com.raredev.vcspace.extension.ExtensionAPIImpl;
import com.raredev.vcspace.extension.completion.JavaCodeCompletionProvider;
import com.raredev.vcspace.fragments.SettingsFragment;
import com.raredev.vcspace.util.ILogger;
import com.raredev.vcspace.util.Utils;
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;
import org.eclipse.tm4e.core.registry.IThemeSource;

public class VCSpaceApplication extends Application {

  private static VCSpaceApplication instance;

  private ShutdownReceiver shutdownReceiver;

  private SharedPreferences defaultPref;

  public static VCSpaceApplication getInstance() {
    return instance;
  }

  @Override
  public void onCreate() {
    instance = this;
    super.onCreate();
    defaultPref = PreferenceManager.getDefaultSharedPreferences(this);
    DynamicColors.applyToActivitiesIfAvailable(this);
    AppCompatDelegate.setDefaultNightMode(SettingsFragment.getThemeFromPrefs());
    Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
    registerShutdownReceiver();
    loadTextMate();

    ExtensionAPI extensionAPI = ExtensionAPIImpl.getInstance();
    extensionAPI.registerExtension("javaCodeCompletion", new JavaCodeCompletionProvider());
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
      String[] themes = new String[] {"vcspace_dark", "quietlight"};
      ThemeRegistry themeRegistry = ThemeRegistry.getInstance();
      for (String name : themes) {
        String path = "textmate/" + name + ".json";
        themeRegistry.loadTheme(
            new ThemeModel(
                IThemeSource.fromInputStream(
                    FileProviderRegistry.getInstance().tryGetInputStream(path), path, null),
                name));
      }
    } catch (Exception e) {
      ILogger.error(
          "ThemeLoader", "Error when trying to load themes: \t" + Log.getStackTraceString(e));
    }
    // Load editor languages
    try {
      GrammarRegistry.getInstance().loadGrammars("textmate/languages.json");
    } catch (Exception e) {
      ILogger.error(
          "LanguageLoader", "Error when trying to load languages: \t" + Log.getStackTraceString(e));
    }
  }

  public SharedPreferences getDefaultPref() {
    return defaultPref;
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
