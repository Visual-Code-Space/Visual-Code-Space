package com.raredev.vcspace.fragments.preferences

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raredev.vcspace.resources.R
import com.raredev.vcspace.utils.PreferencesUtils
import com.raredev.vcspace.utils.PreferencesUtils.prefs
import com.raredev.vcspace.utils.SharedPreferencesKeys

class GeneralPreferencesFragment : PreferenceFragmentCompat() {

  private val themes by lazy {
    arrayOf(
      getString(R.string.pref_aparence_uimode_value_followsys),
      getString(R.string.pref_aparence_uimode_value_dark),
      getString(R.string.pref_aparence_uimode_value_light)
    )
  }

  private val themeValues by lazy { arrayOf("default", "dark", "light") }

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.preferences_general, rootKey)

    findPreference<Preference>(SharedPreferencesKeys.KEY_THEME)?.setOnPreferenceClickListener { _ ->
      val selectedThemeValue = prefs.getString(SharedPreferencesKeys.KEY_THEME, "default")
      MaterialAlertDialogBuilder(requireContext())
        .setTitle(R.string.pref_aparence_uimode)
        .setSingleChoiceItems(themes, themeValues.indexOf(selectedThemeValue)) { d, w ->
          prefs.edit().putString(SharedPreferencesKeys.KEY_THEME, themeValues[w]).apply()
          AppCompatDelegate.setDefaultNightMode(PreferencesUtils.appTheme)
          d.dismiss()
        }
        .setPositiveButton(android.R.string.cancel, null)
        .show()
      true
    }
  }
}
