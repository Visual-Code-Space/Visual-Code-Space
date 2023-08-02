package com.raredev.vcspace;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import com.google.android.material.color.DynamicColors;
import com.raredev.vcspace.activity.BaseActivity;
import com.raredev.vcspace.fragments.settings.GeneralSettingsFragment;
import com.raredev.vcspace.plugin.PluginsLoader;
import com.raredev.vcspace.util.ILogger;
import com.raredev.vcspace.util.PreferencesUtils;
import io.github.rosemoe.sora.langs.textmate.provider.TextMateProvider;
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;

public class VCSpaceApplication extends Application {

  private static VCSpaceApplication instance;

  private SharedPreferences defaultPref;

  public static VCSpaceApplication getInstance() {
    return instance;
  }

  @Override
  public void onCreate() {
    instance = this;
    super.onCreate();
    defaultPref = PreferenceManager.getDefaultSharedPreferences(this);
    AppCompatDelegate.setDefaultNightMode(GeneralSettingsFragment.getThemeFromPrefs());
    if (PreferencesUtils.useDynamicColors()) {
      DynamicColors.applyToActivitiesIfAvailable(this);
    }
    Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
    if (BaseActivity.isPermissionGaranted(this)) {
      PluginsLoader.loadPlugins();
    }
    loadTextMate();
  }

  private void loadTextMate() {
    FileProviderRegistry.getInstance().addFileProvider(new AssetsFileResolver(getAssets()));
    // Load editor languages
    try {
      TextMateProvider.registerLanguages();
    } catch (Exception e) {
      ILogger.error("LanguageLoader", "Error when trying to load languages:", e);
    }
  }

  public SharedPreferences getDefaultPref() {
    return defaultPref;
  }
}
