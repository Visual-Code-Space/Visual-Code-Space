package com.raredev.vcspace.fragments.settings;

import android.os.Bundle;
import android.view.View;
import androidx.preference.PreferenceFragmentCompat;
import com.raredev.vcspace.res.R;
import com.raredev.vcspace.utils.Utils;

public class FileSettingsFragment extends PreferenceFragmentCompat {

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Utils.setActivityTitle(requireActivity(), getString(R.string.file));
  }

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.settings_file, rootKey);
  }
}
