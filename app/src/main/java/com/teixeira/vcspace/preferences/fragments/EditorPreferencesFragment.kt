package com.teixeira.vcspace.preferences.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.teixeira.vcspace.resources.R

class EditorPreferencesFragment : PreferenceFragmentCompat() {

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.preferences_editor, rootKey)
  }
}
