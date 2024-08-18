package com.teixeira.vcspace.preferences.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.teixeira.vcspace.preferences.R

class FilePreferencesFragment : PreferenceFragmentCompat() {

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.preferences_file, rootKey)
  }
}
