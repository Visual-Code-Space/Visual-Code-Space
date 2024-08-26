/*
 * This file is part of Visual Code Space.
 *
 * Visual Code Space is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Visual Code Space is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Visual Code Space.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.teixeira.vcspace.preferences.fragments

import android.os.Bundle
import androidx.fragment.app.commit
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.teixeira.vcspace.R
import com.teixeira.vcspace.app.BaseApplication
import com.teixeira.vcspace.preferences.PREF_ABOUT_GITHUB_KEY
import com.teixeira.vcspace.preferences.PREF_CONFIGURE_EDITOR_KEY
import com.teixeira.vcspace.preferences.PREF_CONFIGURE_FILE_KEY
import com.teixeira.vcspace.preferences.PREF_CONFIGURE_GENERAL_KEY
import com.teixeira.vcspace.preferences.R.xml

/** @author Felipe Teixeira */
class PreferencesFragment : PreferenceFragmentCompat() {

  companion object {
    const val FRAGMENT_TAG = "PreferencesFragment"
  }

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(xml.preferences, rootKey)

    findPreference<Preference>(PREF_CONFIGURE_GENERAL_KEY)?.setOnPreferenceClickListener { _ ->
      gotoPreferenceFragment(GenericPreferencesFragment.create(xml.preferences_general))
      true
    }

    findPreference<Preference>(PREF_CONFIGURE_EDITOR_KEY)?.setOnPreferenceClickListener { _ ->
      gotoPreferenceFragment(GenericPreferencesFragment.create(xml.preferences_editor))
      true
    }

    findPreference<Preference>(PREF_CONFIGURE_FILE_KEY)?.setOnPreferenceClickListener { _ ->
      gotoPreferenceFragment(GenericPreferencesFragment.create(xml.preferences_file))
      true
    }

    findPreference<Preference>(PREF_ABOUT_GITHUB_KEY)?.setOnPreferenceClickListener { _ ->
      BaseApplication.instance.openProjectRepo()
      true
    }
  }

  private fun gotoPreferenceFragment(fragment: PreferenceFragmentCompat) {
    parentFragmentManager.commit {
      replace(R.id.container, fragment)
      addToBackStack(null)
    }
  }
}
