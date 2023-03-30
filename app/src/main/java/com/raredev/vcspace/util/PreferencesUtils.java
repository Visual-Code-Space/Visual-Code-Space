package com.raredev.vcspace.util;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import com.raredev.vcspace.R;
import com.raredev.vcspace.VCSpaceApplication;
import com.raredev.vcspace.managers.SettingsManager;

public class PreferencesUtils {

  public static SharedPreferences getToolsPrefs() {
    return VCSpaceApplication.appContext.getSharedPreferences(SettingsManager.KEY_TOOLS_PREFERENCE, Context.MODE_PRIVATE);
  }

  public static SharedPreferences getDefaultPrefs() {
    return PreferenceManager.getDefaultSharedPreferences(VCSpaceApplication.appContext);
  }

  /*
   * Returns if the user wants to use dynamic theme(Material3)
   */
  public static boolean useDynamicColors() {
    return getDefaultPrefs().getBoolean(SettingsManager.KEY_DYNAMIC_COLORS, true);
  }

  /*
   * Returns whether the user wants the app to open recent files and folders
   */
  public static boolean useOpenRecentsAutomatically() {
    return getDefaultPrefs().getBoolean(SettingsManager.KEY_OPEN_RECENT, false);
  }

  /*
   * Returns the user-selected font value
   */
  public static int getEditorTextSize() {
    return getDefaultPrefs().getInt(SettingsManager.KEY_EDITOR_TEXT_SIZE, 14);
  }

  public static int getEditorTABSize() {
    return Integer.valueOf(getDefaultPrefs().getString(SettingsManager.KEY_EDITOR_TAB_SIZE, "4"));
  }

  /*
   * Returns the font the user wants to use in the editor
   */
  public static int getSelectedFont() {
    String selectedFont = getDefaultPrefs().getString(SettingsManager.KEY_EDITOR_FONT, "firacode");
    return getFont(selectedFont);
  }

  /*
   * Returns whether the user wants to use spaces instead of tabs(\t)
   */
  public static boolean useSpaces() {
    return getDefaultPrefs().getBoolean(SettingsManager.KEY_USE_SPACES, true);
  }

  /*
   * Returns whether the user wants to quickly delete empty lines
   */
  public static boolean useDeleteEmptyLineFast() {
    return getDefaultPrefs().getBoolean(SettingsManager.KEY_DELETE_EMPTY_LINE_FAST, true);
  }

  public static String getTab() {
    String spaces = " ".repeat(getEditorTABSize());
    return useSpaces() ? spaces : "\t";
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
