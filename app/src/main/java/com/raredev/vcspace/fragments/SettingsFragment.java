package com.raredev.vcspace.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.raredev.vcspace.R;
import com.raredev.vcspace.VCSpaceApplication;

public class SettingsFragment extends PreferenceFragmentCompat {

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.settings, rootKey);
    Preference theme = findPreference("theme");
    assert theme != null;
    theme.setOnPreferenceChangeListener(
        (preference, newValue) -> {
          if (newValue instanceof String) {
            VCSpaceApplication androidApp = new VCSpaceApplication();
            var newTheme = androidApp.getTheme((String) newValue);
            AppCompatDelegate.setDefaultNightMode(newTheme);
            return true;
          }
          return false;
        });

    Preference github = findPreference("github");
    github.setOnPreferenceClickListener(
        (pref) -> {
          String url = "https://github.com/raredeveloper/Visual-Code-Space";
          Intent i = new Intent(Intent.ACTION_VIEW);
          i.setData(Uri.parse(url));
          startActivity(i);
          return true;
        });
  }
}
