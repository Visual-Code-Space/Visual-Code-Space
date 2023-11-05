package com.raredev.vcspace.fragments.settings;

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.raredev.vcspace.res.R

class FileSettingsFragment: PreferenceFragmentCompat() {

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.settings_file, rootKey)
  }
}
