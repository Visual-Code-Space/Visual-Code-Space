package com.raredev.vcspace;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import com.google.android.material.color.DynamicColors;
import com.raredev.vcspace.fragments.settings.GeneralSettingsFragment;
import com.raredev.vcspace.plugin.PluginsLoader;
import com.raredev.vcspace.util.ILogger;
import io.github.rosemoe.sora.langs.textmate.provider.TextMateProvider;
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;

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
    AppCompatDelegate.setDefaultNightMode(GeneralSettingsFragment.getThemeFromPrefs());
    Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
    registerShutdownReceiver();
    loadTextMate();

    PluginsLoader.loadPlugins();
  }

  @Override
  public void onTerminate() {
    unregisterShutdownReceiver();
    super.onTerminate();
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
