package com.raredev.vcspace.fragments.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raredev.vcspace.databinding.LayoutMaterialSliderBinding
import com.raredev.vcspace.res.R
import com.raredev.vcspace.utils.PreferencesUtils
import com.raredev.vcspace.utils.PreferencesUtils.prefs
import com.raredev.vcspace.utils.SharedPreferencesKeys

class EditorSettingsFragment: PreferenceFragmentCompat() {

  private val fonts by lazy {
      arrayOf(
        getString(R.string.pref_font_firacode),
        getString(R.string.pref_font_jetbrains)
      )
    }

  private val fontValues by lazy { arrayOf("firacode", "jetbrains") }

  private val tabSizes by lazy { arrayOf("2", "4", "6", "8") }

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.settings_editor, rootKey)

    findPreference<Preference>(SharedPreferencesKeys.KEY_EDITOR_TEXT_SIZE)?.setOnPreferenceClickListener { _ ->
      val binding = LayoutMaterialSliderBinding.inflate(layoutInflater)
      binding.slider.apply {
        valueFrom = 8.0f
        valueTo = 27.0f
        value = PreferencesUtils.textSize.toFloat()
        stepSize = 1.0f
      }

      MaterialAlertDialogBuilder(requireContext())
        .setTitle(R.string.pref_editor_textsize)
        .setPositiveButton(android.R.string.ok, { _, _ ->
          prefs.edit().putInt(
            SharedPreferencesKeys.KEY_EDITOR_TEXT_SIZE,
            binding.slider.value.toInt()
          ).apply()
        })
        .setNegativeButton(android.R.string.cancel, null)
        .setNeutralButton(R.string.reset, { _, _ ->
          prefs.edit().putInt(
            SharedPreferencesKeys.KEY_EDITOR_TEXT_SIZE,
            14
          ).apply()
        })
        .setView(binding.root)
        .show()
      true
    }

    findPreference<Preference>(SharedPreferencesKeys.KEY_EDITOR_TAB_SIZE)?.setOnPreferenceClickListener { _ ->
      val selectedSizePos = when (PreferencesUtils.tabSize) {
        4 -> 1
        6 -> 2
        8 -> 3
        else -> 0
      }
      MaterialAlertDialogBuilder(requireContext())
        .setTitle(R.string.pref_editor_tabsize)
        .setSingleChoiceItems(tabSizes, selectedSizePos, { d, w ->
          prefs.edit().putString(
            SharedPreferencesKeys.KEY_EDITOR_TAB_SIZE,
            tabSizes[w]
          ).apply()
          d.dismiss()
        })
        .setPositiveButton(android.R.string.cancel, null)
        .show()
      true
    }

    findPreference<Preference>(SharedPreferencesKeys.KEY_EDITOR_FONT)?.setOnPreferenceClickListener { _ ->
      val selectedFontName = prefs.getString(SharedPreferencesKeys.KEY_EDITOR_FONT, "firacode")
      MaterialAlertDialogBuilder(requireContext())
        .setTitle(R.string.pref_editor_font)
        .setSingleChoiceItems(fonts, fontValues.indexOf(selectedFontName), { d, w ->
          prefs.edit().putString(
            SharedPreferencesKeys.KEY_EDITOR_FONT,
            fontValues[w]
          ).apply()
          d.dismiss()
        })
        .setPositiveButton(android.R.string.cancel, null)
        .show()
      true
    }
  }
}
