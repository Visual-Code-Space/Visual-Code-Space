package com.raredev.vcspace.fragments.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.raredev.vcspace.resources.R

class FilePreferencesFragment : PreferenceFragmentCompat() {

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.preferences_file, rootKey)
  }
}
