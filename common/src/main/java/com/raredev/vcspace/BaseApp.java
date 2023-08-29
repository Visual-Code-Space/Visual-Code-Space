package com.raredev.vcspace;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import com.raredev.vcspace.util.Environment;

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
    Environment.init();
  }

  public SharedPreferences getDefaultPref() {
    return defaultPref;
  }
}