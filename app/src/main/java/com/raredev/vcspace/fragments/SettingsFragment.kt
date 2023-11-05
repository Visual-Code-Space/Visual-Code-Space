package com.raredev.vcspace.fragments

import android.content.Intent
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.raredev.vcspace.app.BaseApplication
import com.raredev.vcspace.fragments.settings.EditorSettingsFragment
import com.raredev.vcspace.fragments.settings.FileSettingsFragment
import com.raredev.vcspace.fragments.settings.GeneralSettingsFragment
import com.raredev.vcspace.fragments.settings.GitSettingsFragment
import com.raredev.vcspace.utils.SharedPreferencesKeys
import com.raredev.vcspace.res.R

class SettingsFragment: PreferenceFragmentCompat() {

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.settings, rootKey)

    val controller = findNavController()

    findPreference<Preference>(SharedPreferencesKeys.KEY_GENERAL)?.setOnPreferenceClickListener { _ ->
      controller.navigate(SettingsFragmentDirections.actionGoToGeneralSettings())
      true
    }

    findPreference<Preference>(SharedPreferencesKeys.KEY_EDITOR)?.setOnPreferenceClickListener { _ ->
      controller.navigate(SettingsFragmentDirections.actionGoToEditorSettings())
      true
    }

    findPreference<Preference>(SharedPreferencesKeys.KEY_FILE)?.setOnPreferenceClickListener { _ ->
      controller.navigate(SettingsFragmentDirections.actionGoToFileSettings())
      true
    }

    findPreference<Preference>(SharedPreferencesKeys.KEY_GIT)?.setOnPreferenceClickListener { _ ->
      controller.navigate(SettingsFragmentDirections.actionGoToGitSettings())
      true
    }

    findPreference<Preference>(SharedPreferencesKeys.KEY_GITHUB)?.setOnPreferenceClickListener { _ ->
      BaseApplication.getInstance().openProjectRepo()
      true
    }
    findPreference<Preference>(SharedPreferencesKeys.KEY_LICENSES)?.setOnPreferenceClickListener { _ ->
      startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
      true
    }
  }
}
