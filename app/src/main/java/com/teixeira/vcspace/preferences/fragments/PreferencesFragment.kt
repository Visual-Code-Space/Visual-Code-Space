package com.teixeira.vcspace.preferences.fragments

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.teixeira.vcspace.R
import com.teixeira.vcspace.app.BaseApplication
import com.teixeira.vcspace.preferences.PREF_ABOUT_GITHUB_KEY
import com.teixeira.vcspace.preferences.PREF_CONFIGURE_EDITOR_KEY
import com.teixeira.vcspace.preferences.PREF_CONFIGURE_FILE_KEY
import com.teixeira.vcspace.preferences.PREF_CONFIGURE_GENERAL_KEY
import com.teixeira.vcspace.resources.R.xml

class PreferencesFragment : PreferenceFragmentCompat() {

  companion object {
    const val FRAGMENT_TAG = "PreferencesFragment"
  }

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(xml.preferences, rootKey)

    findPreference<Preference>(PREF_CONFIGURE_GENERAL_KEY)?.setOnPreferenceClickListener { _ ->
      goToPreferenceFragment(GeneralPreferencesFragment())
      true
    }

    findPreference<Preference>(PREF_CONFIGURE_EDITOR_KEY)?.setOnPreferenceClickListener { _ ->
      goToPreferenceFragment(EditorPreferencesFragment())
      true
    }

    findPreference<Preference>(PREF_CONFIGURE_FILE_KEY)?.setOnPreferenceClickListener { _ ->
      goToPreferenceFragment(FilePreferencesFragment())
      true
    }

    findPreference<Preference>(PREF_ABOUT_GITHUB_KEY)?.setOnPreferenceClickListener { _ ->
      BaseApplication.instance.openProjectRepo()
      true
    }
  }

  private fun goToPreferenceFragment(fragment: PreferenceFragmentCompat) {
    parentFragmentManager
      .beginTransaction()
      .replace(R.id.container, fragment)
      .addToBackStack(null)
      .commit()
  }
}
