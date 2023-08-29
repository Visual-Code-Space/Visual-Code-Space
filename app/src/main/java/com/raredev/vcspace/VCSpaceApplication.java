package com.raredev.vcspace;

import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.color.DynamicColors;
import com.raredev.vcspace.fragments.settings.GeneralSettingsFragment;
import com.raredev.vcspace.util.ILogger;
import com.raredev.vcspace.util.PreferencesUtils;
import io.github.rosemoe.sora.langs.textmate.provider.TextMateProvider;
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;

public class VCSpaceApplication extends BaseApp {

  @Override
  public void onCreate() {
    super.onCreate();
    Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
    AppCompatDelegate.setDefaultNightMode(GeneralSettingsFragment.getThemeFromPrefs());
    if (PreferencesUtils.useDynamicColors()) {
      DynamicColors.applyToActivitiesIfAvailable(this);
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
}
