package com.raredev.vcspace.util;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import com.raredev.vcspace.VCSpaceApplication;
import com.raredev.vcspace.R;

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

  public static int getSelectedFont() {
    String selectedFont = getDefaultPrefs().getString("editorfont", "firacode");
    return getFont(selectedFont);
  }

  public static boolean isSoftTab() {
    return getDefaultPrefs().getBoolean("softtab", false);
  }

  public static boolean isDeleleteEmptyLineFast() {
    return getDefaultPrefs().getBoolean("deletefast", false);
  }

  private static int getFont(String selectedTheme) {
    switch (selectedTheme) {
      case "firacode":
        return R.font.firacode_regular;
      case "jetbrains":
        return R.font.jetbrains_mono;
      default:
        return R.font.jetbrains_mono;
    }
  }
}
