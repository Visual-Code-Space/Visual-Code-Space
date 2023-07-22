package com.raredev.vcspace.fragments.settings;

import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.raredev.vcspace.R;
import com.raredev.vcspace.databinding.LayoutMaterialSliderBinding;
import com.raredev.vcspace.util.PreferencesUtils;
import com.raredev.vcspace.util.SharedPreferencesKeys;

public class EditorSettingsFragment extends PreferenceFragmentCompat {

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.settings_editor, rootKey);

    Preference fontSize = findPreference(SharedPreferencesKeys.KEY_FONT_SIZE_PREFERENCE);
    fontSize.setOnPreferenceClickListener(
        (pref) -> {
          LayoutMaterialSliderBinding binding =
              LayoutMaterialSliderBinding.inflate(getLayoutInflater());
          binding.slider.setValueFrom(8.0f);
          binding.slider.setValueTo(27.0f);
          binding.slider.setValue(PreferencesUtils.getEditorTextSize());
          binding.slider.setStepSize(1.0f);

          var prefs = PreferencesUtils.getDefaultPrefs();

          new MaterialAlertDialogBuilder(requireContext())
              .setTitle(R.string.pref_editor_textsize)
              .setMessage(R.string.choose_default_font)
              .setPositiveButton(
                  android.R.string.ok,
                  (d, w) ->
                      prefs
                          .edit()
                          .putInt(
                              SharedPreferencesKeys.KEY_EDITOR_TEXT_SIZE, (int) binding.slider.getValue())
                          .apply())
              .setNegativeButton(android.R.string.cancel, (d, w) -> d.dismiss())
              .setNeutralButton(
                  R.string.reset,
                  (d, w) -> prefs.edit().putInt(SharedPreferencesKeys.KEY_EDITOR_TEXT_SIZE, 14).apply())
              .setView(binding.getRoot())
              .setCancelable(false)
              .show();
          return true;
        });
  }
}
