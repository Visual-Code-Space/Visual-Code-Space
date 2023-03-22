package com.raredev.vcspace.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.raredev.vcspace.VCSpaceApplication;
import com.raredev.vcspace.R;

public class PreferencesUtils {
  public static final String KEY_RECENT_FOLDER = "recentFolderPath";

  public static SharedPreferences getToolsPrefs() {
    return VCSpaceApplication.appContext.getSharedPreferences("tools", Activity.MODE_PRIVATE);
  }

  public static SharedPreferences getDefaultPrefs() {
    return PreferenceManager.getDefaultSharedPreferences(VCSpaceApplication.appContext);
  }

  /*
   * Returns if the user wants to use dynamic theme(Material3)
   */
  public static boolean useDynamicColors() {
    return getDefaultPrefs().getBoolean("pref_dynamiccolors", true);
  }

  /*
   * Returns whether the user wants the app to open recent files and folders
   */
  public static boolean useOpenRecentsAutomatically() {
    return getDefaultPrefs().getBoolean("pref_openrecentsautomatically", false);
  }

  /*
   * Returns the user-selected font value
   */
  public static int getEditorTextSize() {
    return getDefaultPrefs().getInt("pref_editortextsize", 14);
  }

  public static int getEditorTABSize() {
    return Integer.valueOf(getDefaultPrefs().getString("pref_editortabsize", "4"));
  }

  /*
   * Returns the font the user wants to use in the editor
   */
  public static int getSelectedFont() {
    String selectedFont = getDefaultPrefs().getString("pref_editorfont", "firacode");
    return getFont(selectedFont);
  }

  /*
   * Returns whether the user wants to use spaces instead of tabs(\t)
   */
  public static boolean useUseSpaces() {
    return getDefaultPrefs().getBoolean("pref_usespaces", true);
  }

  /*
   * Returns whether the user wants to quickly delete empty lines
   */
  public static boolean useDeleteEmptyLineFast() {
    return getDefaultPrefs().getBoolean("pref_deleteemptylinefast", true);
  }

  public static String getTab() {
    String spaces = " ".repeat(getEditorTABSize());
    return useUseSpaces() ? spaces : "\t";
  }

  /*
   * Returns the font selected by the user
   */
  private static int getFont(String selectedFont) {
    switch (selectedFont) {
      case "firacode":
        return R.font.firacode_regular;
      case "jetbrains":
        return R.font.jetbrains_mono;
      default:
        return R.font.firacode_regular;
    }
  }
}
