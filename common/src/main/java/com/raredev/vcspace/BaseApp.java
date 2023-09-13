package com.raredev.vcspace;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import androidx.preference.PreferenceManager;
import com.raredev.vcspace.utils.Environment;

public class BaseApp extends Application {

  public static final String REPO_URL = "https://github.com/Visual-Code-Space/Visual-Code-Space";

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

  public void openProjectRepo() {
    openUrl(REPO_URL);
  }

  public void openUrl(String url) {
    try {
      Intent open = new Intent();
      open.setAction(Intent.ACTION_VIEW);
      open.setData(Uri.parse(url));
      open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(open);
    } catch (Throwable th) {
      th.printStackTrace();
    }
  }
}
