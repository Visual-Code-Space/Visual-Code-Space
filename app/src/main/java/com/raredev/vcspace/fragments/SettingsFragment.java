package com.raredev.vcspace.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.raredev.vcspace.R;

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
  }
}
