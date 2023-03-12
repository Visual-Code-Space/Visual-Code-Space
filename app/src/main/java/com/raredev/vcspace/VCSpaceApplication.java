package com.raredev.vcspace;

import android.app.Application;
import android.content.Context;
import com.google.android.material.color.DynamicColors;
import com.raredev.vcspace.util.PreferencesUtils;

public class VCSpaceApplication extends Application {
  public static Context appContext;

  @Override
  public void onCreate() {
    super.onCreate();
    appContext = this;
    Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
    if (PreferencesUtils.useDynamicColors()) {
      DynamicColors.applyToActivitiesIfAvailable(this);
    }
  }
}
