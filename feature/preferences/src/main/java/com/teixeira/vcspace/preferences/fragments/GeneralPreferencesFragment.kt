package com.teixeira.vcspace.preferences.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.teixeira.vcspace.preferences.R

class GeneralPreferencesFragment : PreferenceFragmentCompat() {

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.preferences_general, rootKey)
  }
}
