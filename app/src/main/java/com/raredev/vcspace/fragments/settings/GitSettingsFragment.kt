package com.raredev.vcspace.fragments.settings

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raredev.vcspace.app.BaseApplication
import com.raredev.vcspace.databinding.LayoutCredentialBinding
import com.raredev.vcspace.res.R
import com.raredev.vcspace.utils.PreferencesUtils.prefs
import com.raredev.vcspace.utils.SharedPreferencesKeys

class GitSettingsFragment : PreferenceFragmentCompat() {

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.settings_git, rootKey)

    findPreference<Preference>(SharedPreferencesKeys.KEY_CREDENTIAL)
      ?.setOnPreferenceClickListener { _ ->
        showCredentialDialog()
        true
      }
  }

  private fun showCredentialDialog() {
    val binding = LayoutCredentialBinding.inflate(LayoutInflater.from(requireContext()))
    val helper = binding.helper
    val content = helper.text.toString()
    val linkText = requireContext().getString(R.string.github_token)
    val startIndex = content.indexOf(linkText)
    val endIndex = startIndex + linkText.length

    val spannableString = SpannableString(content)
    spannableString.setSpan(
      object : ClickableSpan() {
        override fun onClick(textView: View) {
          val url = "https://github.com/settings/tokens"
          BaseApplication.instance.openUrl(url)
        }
      },
      startIndex,
      endIndex,
      Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    helper.text = spannableString
    helper.movementMethod = LinkMovementMethod.getInstance()
    helper.highlightColor = Color.TRANSPARENT

    binding.etInputUsername.setText(
      prefs.getString(SharedPreferencesKeys.KEY_CREDENTIAL_USERNAME, "")
    )
    binding.etInputPassword.setText(
      prefs.getString(SharedPreferencesKeys.KEY_CREDENTIAL_PASSWORD, "")
    )

    MaterialAlertDialogBuilder(requireContext())
      .setTitle(R.string.pref_git_credentials)
      .setPositiveButton(android.R.string.ok) { _, _ ->
        val editor = prefs.edit()
        editor.putString(
          SharedPreferencesKeys.KEY_CREDENTIAL_USERNAME,
          binding.etInputUsername.text.toString()
        )
        editor.putString(
          SharedPreferencesKeys.KEY_CREDENTIAL_PASSWORD,
          binding.etInputPassword.text.toString()
        )
        editor.apply()
      }
      .setView(binding.root)
      .show()
  }
}
