package com.raredev.vcspace;

import android.content.Intent;
import androidx.appcompat.app.AppCompatDelegate;
import com.blankj.utilcode.util.ThrowableUtils;
import com.google.android.material.color.DynamicColors;
import com.raredev.vcspace.activity.CrashActivity;
import com.raredev.vcspace.fragments.settings.GeneralSettingsFragment;
import com.raredev.vcspace.util.PreferencesUtils;
import io.github.rosemoe.sora.langs.textmate.provider.TextMateProvider;
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;

public class VCSpaceApplication extends BaseApp implements Thread.UncaughtExceptionHandler {

  private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

  @Override
  public void onCreate() {
    uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
    Thread.setDefaultUncaughtExceptionHandler(this);
    super.onCreate();
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
      e.printStackTrace();
    }
  }

  @Override
  public void uncaughtException(Thread thread, Throwable th) {
    try {
      var intent = new Intent(this, CrashActivity.class);

      // Add the error message
      intent.putExtra(CrashActivity.KEY_EXTRA_ERROR, ThrowableUtils.getFullStackTrace(th));
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      // Start the crash activity
      startActivity(intent);
      if (uncaughtExceptionHandler != null) {
        uncaughtExceptionHandler.uncaughtException(thread, th);
      }
      System.exit(1);
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }
}
