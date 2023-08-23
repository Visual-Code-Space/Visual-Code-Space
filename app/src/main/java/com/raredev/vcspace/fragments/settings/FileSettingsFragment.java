package com.raredev.vcspace.fragments.settings;

import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.raredev.vcspace.res.R;
import com.raredev.vcspace.util.PreferencesUtils;
import com.raredev.vcspace.util.SharedPreferencesKeys;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class FileSettingsFragment extends PreferenceFragmentCompat {

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.settings_file, rootKey);

    Preference fontSize = findPreference(SharedPreferencesKeys.KEY_ENCODING_FOR_OPENING);
    fontSize.setOnPreferenceClickListener(
        (pref) -> {
          var encodingsBuilder = new MaterialAlertDialogBuilder(requireContext());
          encodingsBuilder.setTitle(R.string.pref_encoding_for_opening);

          List<String> entries = getSupportedEncodings();

          var prefs = PreferencesUtils.getDefaultPrefs();

          int i =
              entries.indexOf(
                  prefs.getString(SharedPreferencesKeys.KEY_ENCODING_FOR_OPENING, "UTF-8"));

          encodingsBuilder.setSingleChoiceItems(
              entries.toArray(new String[0]),
              i,
              (dlg, which) -> {
                prefs
                    .edit()
                    .putString(SharedPreferencesKeys.KEY_ENCODING_FOR_OPENING, entries.get(which))
                    .apply();
                dlg.cancel();
              });
          encodingsBuilder.setPositiveButton(android.R.string.cancel, null);
          encodingsBuilder.setCancelable(false);
          encodingsBuilder.show();
          return true;
        });
  }

  private List<String> getSupportedEncodings() {
    var entries = new ArrayList<String>();
    var supportedEncodings = Charset.availableCharsets();

    for (String entry : supportedEncodings.keySet()) {
      entries.add(entry);
    }
    return entries;
  }
}
