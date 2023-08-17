package com.raredev.vcspace.fragments.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.raredev.vcspace.databinding.LayoutMaterialSliderBinding;
import com.raredev.vcspace.res.R;
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
                              SharedPreferencesKeys.KEY_EDITOR_TEXT_SIZE,
                              (int) binding.slider.getValue())
                          .apply())
              .setNegativeButton(android.R.string.cancel, (d, w) -> d.dismiss())
              .setNeutralButton(
                  R.string.reset,
                  (d, w) ->
                      prefs.edit().putInt(SharedPreferencesKeys.KEY_EDITOR_TEXT_SIZE, 14).apply())
              .setView(binding.getRoot())
              .setCancelable(false)
              .show();
          return true;
        });

    Preference tabSize = findPreference(SharedPreferencesKeys.KEY_EDITOR_TAB_SIZE);
    tabSize.setOnPreferenceClickListener(
        (pref) -> {
          String[] tabSizes = {"2", "4", "6", "8"};

          var selectTabSizeBuilder = new MaterialAlertDialogBuilder(requireContext());
          selectTabSizeBuilder.setTitle(R.string.pref_editor_tabsize);

          SharedPreferences prefs = PreferencesUtils.getDefaultPrefs();

          var selectedSize = prefs.getString(SharedPreferencesKeys.KEY_EDITOR_TAB_SIZE_VALUE, "4");
          var i = 0;
          if (selectedSize.equals("4")) {
            i = 1;
          } else if (selectedSize.equals("6")) {
            i = 2;
          } else if (selectedSize.equals("8")) {
            i = 3;
          }
          selectTabSizeBuilder.setSingleChoiceItems(
              tabSizes,
              i,
              (dlg, which) -> {
                prefs
                    .edit()
                    .putString(SharedPreferencesKeys.KEY_EDITOR_TAB_SIZE_VALUE, tabSizes[which])
                    .apply();
                dlg.cancel();
              });
          selectTabSizeBuilder.setPositiveButton(android.R.string.cancel, null);
          selectTabSizeBuilder.setCancelable(false);
          selectTabSizeBuilder.show();
          return true;
        });

    Preference font = findPreference(SharedPreferencesKeys.KEY_EDITOR_FONT);
    font.setOnPreferenceClickListener(
        (pref) -> {
          String[] fonts = {
            getString(R.string.pref_font_firacode), getString(R.string.pref_font_jetbrains)
          };

          String[] fontValues = {"firacode", "jetbrains"};
          var selectFontBuilder = new MaterialAlertDialogBuilder(requireContext());
          selectFontBuilder.setTitle(R.string.pref_editor_font);

          SharedPreferences prefs = PreferencesUtils.getDefaultPrefs();

          var selectedFont =
              prefs.getString(SharedPreferencesKeys.KEY_EDITOR_FONT_VALUE, "firacode");
          var i = 0;
          if (selectedFont.equals("jetbrains")) {
            i = 1;
          }
          selectFontBuilder.setSingleChoiceItems(
              fonts,
              i,
              (dlg, which) -> {
                prefs
                    .edit()
                    .putString(SharedPreferencesKeys.KEY_EDITOR_FONT_VALUE, fontValues[which])
                    .apply();
                dlg.cancel();
              });
          selectFontBuilder.setPositiveButton(android.R.string.cancel, null);
          selectFontBuilder.setCancelable(false);
          selectFontBuilder.show();
          return true;
        });
  }
}
