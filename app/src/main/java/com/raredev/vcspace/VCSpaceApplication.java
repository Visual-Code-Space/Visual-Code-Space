package com.raredev.vcspace;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class VCSpaceApplication extends Application {
  public static Context appContext;

  @Override
  public void onCreate() {
    super.onCreate();
    appContext = this;
    Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
  }
}
