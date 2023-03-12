package com.raredev.vcspace.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.blankj.utilcode.util.ToastUtils;
import com.raredev.vcspace.R;
import com.raredev.vcspace.util.PreferencesUtils;

public class SettingsFragment extends PreferenceFragmentCompat {

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.settings, rootKey);

    Preference github = findPreference("github");
    github.setOnPreferenceClickListener(
        (pref) -> {
          String url = "https://github.com/raredeveloperofc/Visual-Code-Space";
          Intent i = new Intent(Intent.ACTION_VIEW);
          i.setData(Uri.parse(url));
          startActivity(i);
          return true;
        });
    Preference theme = findPreference("pref_theme");
    theme.setOnPreferenceChangeListener(
        (preference, newValue) -> {
          AppCompatDelegate.setDefaultNightMode(getTheme((String)newValue));
          return true;
        });
  }

  public static int getThemeFromPrefs() {
    SharedPreferences prefs = PreferencesUtils.getDefaultPrefs();
    String selectedTheme = prefs.getString("pref_theme", "default");
    return getTheme(selectedTheme);
  }

  private static int getTheme(String selectedTheme) {
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
