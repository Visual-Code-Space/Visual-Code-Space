package com.raredev.vcspace.fragments.preferences

import android.content.Intent
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.raredev.vcspace.app.BaseApplication
import com.raredev.vcspace.res.R
import com.raredev.vcspace.utils.SharedPreferencesKeys

class PreferencesFragment : PreferenceFragmentCompat() {

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.preferences, rootKey)

    val controller = findNavController()

    findPreference<Preference>(SharedPreferencesKeys.KEY_GENERAL)?.setOnPreferenceClickListener { _
      ->
      controller.navigate(PreferencesFragmentDirections.actionGoToGeneralPreferences())
      true
    }

    findPreference<Preference>(SharedPreferencesKeys.KEY_EDITOR)?.setOnPreferenceClickListener { _
      ->
      controller.navigate(PreferencesFragmentDirections.actionGoToEditorPreferences())
      true
    }

    findPreference<Preference>(SharedPreferencesKeys.KEY_FILE)?.setOnPreferenceClickListener { _ ->
      controller.navigate(PreferencesFragmentDirections.actionGoToFilePreferences())
      true
    }

    findPreference<Preference>(SharedPreferencesKeys.KEY_GITHUB)?.setOnPreferenceClickListener { _
      ->
      BaseApplication.instance.openProjectRepo()
      true
    }
  }
}
