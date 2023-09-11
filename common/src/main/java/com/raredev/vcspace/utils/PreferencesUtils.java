package com.raredev.vcspace.utils;

import android.content.SharedPreferences;
import com.raredev.vcspace.BaseApp;
import com.raredev.vcspace.res.R;

public class PreferencesUtils {

  public static SharedPreferences getDefaultPrefs() {
    return BaseApp.getInstance().getDefaultPref();
  }

  /*
   * Returns if the user wants to use dynamic theme(Material3)
   */
  public static boolean useDynamicColors() {
    return getDefaultPrefs().getBoolean(SharedPreferencesKeys.KEY_DYNAMIC_COLORS, true);
  }

  /*
   * Returns the user-selected font value
   */
  public static int getEditorTextSize() {
    return getDefaultPrefs().getInt(SharedPreferencesKeys.KEY_EDITOR_TEXT_SIZE, 14);
  }

  public static int getEditorLineHeight() {
    return getDefaultPrefs().getInt(SharedPreferencesKeys.KEY_EDITOR_LINEHEIGHT, 3);
  }

  public static int getEditorTABSize() {
    return Integer.valueOf(
        getDefaultPrefs().getString(SharedPreferencesKeys.KEY_EDITOR_TAB_SIZE, "4"));
  }

  /*
   * Returns the font the user wants to use in the editor
   */
  public static int getSelectedFont() {
    String selectedFont =
        getDefaultPrefs().getString(SharedPreferencesKeys.KEY_EDITOR_FONT, "firacode");
    return getFont(selectedFont);
  }

  public static boolean useStickyScroll() {
    return getDefaultPrefs().getBoolean(SharedPreferencesKeys.KEY_STICKYSCROLL, false);
  }

  public static boolean useFontLigatures() {
    return getDefaultPrefs().getBoolean(SharedPreferencesKeys.KEY_FONTLIGATURES, true);
  }

  public static boolean useWordWrap() {
    return getDefaultPrefs().getBoolean(SharedPreferencesKeys.KEY_WORDWRAP, false);
  }

  public static boolean lineNumbers() {
    return getDefaultPrefs().getBoolean(SharedPreferencesKeys.KEY_LINENUMBERS, true);
  }

  /*
   * Returns whether the user wants to use spaces instead of tabs(\t)
   */
  public static boolean useSpaces() {
    return getDefaultPrefs().getBoolean(SharedPreferencesKeys.KEY_USE_SPACES, true);
  }

  /*
   * Returns whether the user wants to quickly delete empty lines
   */
  public static boolean useDeleteEmptyLineFast() {
    return getDefaultPrefs().getBoolean(SharedPreferencesKeys.KEY_DELETE_EMPTY_LINE_FAST, true);
  }

  public static boolean useDeleteTabs() {
    return getDefaultPrefs().getBoolean(SharedPreferencesKeys.KEY_DELETETABS, true);
  }

  public static String getIndentationString() {
    String spaces = " ".repeat(getEditorTABSize());
    return useSpaces() ? spaces : "\t";
  }

  public static boolean showFilePath() {
    return getDefaultPrefs().getBoolean(SharedPreferencesKeys.KEY_FILE_PATH, true);
  }

  /*
   * Returns whether the user wants to automatically save the file
   */
  public static boolean autoSave() {
    return getDefaultPrefs().getBoolean(SharedPreferencesKeys.KEY_AUTO_SAVE, false);
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

  public static String getEncodingForOpening() {
    return getDefaultPrefs().getString(SharedPreferencesKeys.KEY_ENCODING_FOR_OPENING, "UTF-8");
  }

  public static boolean showHiddenFiles() {
    return getDefaultPrefs().getBoolean(SharedPreferencesKeys.KEY_SHOW_HIDDEN_FILES, true);
  }
}
