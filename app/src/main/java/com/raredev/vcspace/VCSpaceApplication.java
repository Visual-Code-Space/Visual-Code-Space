package com.raredev.vcspace;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.raredev.common.util.*;

import java.io.*;

public class VCSpaceApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    setupTheme();
    Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
  }

  private void setupTheme() {
    int theme = getThemeFromPreferences();
    AppCompatDelegate.setDefaultNightMode(theme);
  }

  public int getThemeFromPreferences() {
    SharedPreferences preferences =
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    String selectedTheme = preferences.getString("theme", "default");
    return getTheme(selectedTheme);
  }

  public String getDescriptionForTheme(String selectedTheme) {
    switch (selectedTheme) {
      case "light":
        return this.getString(R.string.pref_theme_light);
      case "night":
        return this.getString(R.string.pref_theme_dark);
      default:
        return this.getString(R.string.pref_theme_system);
    }
  }

  public int getTheme(String selectedTheme) {
    switch (selectedTheme) {
      case "light":
        return AppCompatDelegate.MODE_NIGHT_NO;
      case "dark":
        return AppCompatDelegate.MODE_NIGHT_YES;
      default:
        return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    }
  }
}
