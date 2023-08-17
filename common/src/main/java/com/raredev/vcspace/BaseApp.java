package com.raredev.vcspace;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

public class BaseApp extends Application {

  private static BaseApp instance;

  private SharedPreferences defaultPref;

  public static BaseApp getInstance() {
    return instance;
  }

  @Override
  public void onCreate() {
    instance = this;
    super.onCreate();
    defaultPref = PreferenceManager.getDefaultSharedPreferences(this);
  }

  public SharedPreferences getDefaultPref() {
    return defaultPref;
  }
}