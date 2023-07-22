package com.raredev.vcspace.fragments.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.raredev.vcspace.R;
import com.raredev.vcspace.util.PreferencesUtils;
import com.raredev.vcspace.util.SharedPreferencesKeys;

public class GeneralSettingsFragment extends PreferenceFragmentCompat {

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.settings_general, rootKey);

    Preference theme = findPreference(SharedPreferencesKeys.KEY_THEME);
    theme.setOnPreferenceChangeListener(
        (preference, newValue) -> {
          AppCompatDelegate.setDefaultNightMode(getTheme((String) newValue));
          return true;
        });
  }
  
  public static int getThemeFromPrefs() {
    SharedPreferences prefs = PreferencesUtils.getDefaultPrefs();
    String selectedTheme = prefs.getString(SharedPreferencesKeys.KEY_THEME, "default");
    return getTheme(selectedTheme);
  }

  public static int getTheme(String selectedTheme) {
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
