package com.raredev.vcspace.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.raredev.vcspace.VCSpaceApplication;

public class PreferencesUtils {

  public static SharedPreferences getDefaultPrefs() {
    return PreferenceManager.getDefaultSharedPreferences(VCSpaceApplication.appContext);
  }
  
  public static int getTextSize() {
    try {
      return Integer.parseInt(getDefaultPrefs().getString("textsize", "14"));
    } catch (Exception e) {
      return 14;
    }
  }
  
  public static boolean isSoftTab() {
    return getDefaultPrefs().getBoolean("softtab", true);
  }
  
  public static boolean isDeleleteEmptyLineFast() {
    return getDefaultPrefs().getBoolean("deletefast", true);
  }
}
