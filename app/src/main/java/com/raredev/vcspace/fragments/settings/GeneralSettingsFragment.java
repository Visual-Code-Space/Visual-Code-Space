package com.raredev.vcspace.fragments.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.raredev.vcspace.res.R;
import com.raredev.vcspace.utils.PreferencesUtils;
import com.raredev.vcspace.utils.SharedPreferencesKeys;
import com.raredev.vcspace.utils.Utils;

public class GeneralSettingsFragment extends PreferenceFragmentCompat {

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Utils.setActivityTitle(requireActivity(), getString(R.string.general));
  }

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.settings_general, rootKey);

    Preference theme = findPreference(SharedPreferencesKeys.KEY_THEME);

    theme.setOnPreferenceClickListener(
        (pref) -> {
          String[] themes = {
            getString(R.string.pref_theme_system),
            getString(R.string.pref_theme_dark),
            getString(R.string.pref_theme_light)
          };

          String[] themeValues = {"default", "dark", "light"};
          var selectThemeBuilder = new MaterialAlertDialogBuilder(requireContext());
          selectThemeBuilder.setTitle(R.string.title_theme);

          SharedPreferences prefs = PreferencesUtils.INSTANCE.getPrefs();

          var selectedTheme = prefs.getString(SharedPreferencesKeys.KEY_THEME, "default");
          var i = 0;
          if (selectedTheme.equals("dark")) {
            i = 1;
          } else if (selectedTheme.equals("light")) {
            i = 2;
          }
          selectThemeBuilder.setSingleChoiceItems(
              themes,
              i,
              (dlg, which) -> {
                prefs.edit().putString(SharedPreferencesKeys.KEY_THEME, themeValues[which]).apply();

                AppCompatDelegate.setDefaultNightMode(PreferencesUtils.INSTANCE.getAppTheme());
                dlg.cancel();
              });
          selectThemeBuilder.setPositiveButton(android.R.string.cancel, null);
          selectThemeBuilder.show();
          return true;
        });
  }
}
