package com.raredev.vcspace.fragments;

import static com.raredev.vcspace.res.R.string;
import static com.raredev.vcspace.res.R.xml;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.raredev.vcspace.R;
import com.raredev.vcspace.fragments.settings.EditorSettingsFragment;
import com.raredev.vcspace.fragments.settings.FileSettingsFragment;
import com.raredev.vcspace.fragments.settings.GeneralSettingsFragment;
import com.raredev.vcspace.fragments.settings.GitSettingsFragment;
import com.raredev.vcspace.util.SharedPreferencesKeys;
import com.raredev.vcspace.util.Utils;

public class SettingsFragment extends PreferenceFragmentCompat {

  public static final String TAG = "SettingsFragment";

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Utils.setActivityTitle(requireActivity(), getString(string.menu_settings));
  }

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(xml.settings, rootKey);

    var generalSettings = new GeneralSettingsFragment();
    var editorSettings = new EditorSettingsFragment();
    var fileSettings = new FileSettingsFragment();
    var gitSettings = new GitSettingsFragment();

    Preference general = findPreference(SharedPreferencesKeys.KEY_GENERAL);
    general.setOnPreferenceClickListener(
        (pref) -> {
          goToFragment(generalSettings);
          return true;
        });

    Preference editor = findPreference(SharedPreferencesKeys.KEY_EDITOR);
    editor.setOnPreferenceClickListener(
        (pref) -> {
          goToFragment(editorSettings);
          return true;
        });

    Preference file = findPreference(SharedPreferencesKeys.KEY_FILE);
    file.setOnPreferenceClickListener(
        (pref) -> {
          goToFragment(fileSettings);
          return true;
        });

    Preference git = findPreference(SharedPreferencesKeys.KEY_GIT);
    git.setOnPreferenceClickListener(
        (pref) -> {
          goToFragment(gitSettings);
          return true;
        });

    Preference github = findPreference(SharedPreferencesKeys.KEY_GITHUB);
    github.setOnPreferenceClickListener(
        (pref) -> {
          openCustomTab("https://github.com/Visual-Code-Space/Visual-Code-Space");
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

  private void goToFragment(Fragment fragment) {
    getParentFragmentManager()
        .beginTransaction()
        .replace(R.id.settings_container, fragment)
        .addToBackStack(null)
        .commit();
  }

  private void openCustomTab(String url) {
    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
    CustomTabsIntent customTabsIntent = builder.build();
    customTabsIntent.launchUrl(requireContext(), Uri.parse(url));
  }
}
