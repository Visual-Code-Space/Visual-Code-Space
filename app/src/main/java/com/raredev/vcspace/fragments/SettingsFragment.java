package com.raredev.vcspace.fragments;

import static com.raredev.vcspace.res.R.xml;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.raredev.vcspace.R;
import com.raredev.vcspace.fragments.settings.EditorSettingsFragment;
import com.raredev.vcspace.fragments.settings.FileSettingsFragment;
import com.raredev.vcspace.fragments.settings.GeneralSettingsFragment;
import com.raredev.vcspace.fragments.settings.GitSettingsFragment;
import com.raredev.vcspace.util.SharedPreferencesKeys;

public class SettingsFragment extends PreferenceFragmentCompat {

  public static final String TAG = "SettingsFragment";

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(xml.settings, rootKey);

    Preference general = findPreference(SharedPreferencesKeys.KEY_GENERAL);
    general.setOnPreferenceClickListener(
        (pref) -> {
          getParentFragmentManager()
              .beginTransaction()
              .replace(R.id.settings_container, new GeneralSettingsFragment())
              .addToBackStack(null)
              .commit();
          return true;
        });

    Preference editor = findPreference(SharedPreferencesKeys.KEY_EDITOR);
    editor.setOnPreferenceClickListener(
        (pref) -> {
          getParentFragmentManager()
              .beginTransaction()
              .replace(R.id.settings_container, new EditorSettingsFragment())
              .addToBackStack(null)
              .commit();
          return true;
        });

    Preference file = findPreference(SharedPreferencesKeys.KEY_FILE);
    file.setOnPreferenceClickListener(
        (pref) -> {
          getParentFragmentManager()
              .beginTransaction()
              .replace(R.id.settings_container, new FileSettingsFragment())
              .addToBackStack(null)
              .commit();
          return true;
        });

    Preference git = findPreference(SharedPreferencesKeys.KEY_GIT);
    git.setOnPreferenceClickListener(
        (pref) -> {
          getParentFragmentManager()
              .beginTransaction()
              .replace(R.id.settings_container, new GitSettingsFragment())
              .addToBackStack(null)
              .commit();
          return true;
        });

    Preference github = findPreference(SharedPreferencesKeys.KEY_GITHUB);
    github.setOnPreferenceClickListener(
        (pref) -> {
          String url = "https://github.com/Visual-Code-Space/Visual-Code-Space";
          Intent i = new Intent(Intent.ACTION_VIEW);
          i.setData(Uri.parse(url));
          startActivity(i);
          return true;
        });
    Preference licenses = findPreference(SharedPreferencesKeys.KEY_LICENSES);
    licenses.setOnPreferenceClickListener(
        (pref) -> {
          Intent i = new Intent(requireContext(), OssLicensesMenuActivity.class);
          startActivity(i);
          return true;
        });
  }
}
