package com.raredev.vcspace;

import android.app.Application;
import android.content.Context;

public class VCSpaceApplication extends Application {
  public static Context appContext;

  @Override
  public void onCreate() {
    super.onCreate();
    appContext = this;
    Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
  }
}
