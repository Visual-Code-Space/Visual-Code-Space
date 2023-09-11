package com.raredev.vcspace.fragments.settings;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.raredev.vcspace.databinding.LayoutCredentialBinding;
import com.raredev.vcspace.res.R;
import com.raredev.vcspace.utils.PreferencesUtils;
import com.raredev.vcspace.utils.SharedPreferencesKeys;
import com.raredev.vcspace.utils.Utils;

public class GitSettingsFragment extends PreferenceFragmentCompat {

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Utils.setActivityTitle(requireActivity(), getString(R.string.git));
  }

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.settings_git, rootKey);

    Preference credential = findPreference(SharedPreferencesKeys.KEY_CREDENTIAL);
    credential.setOnPreferenceClickListener(
        (pref) -> {
          showCredentialDialog(requireContext());
          return true;
        });
  }

  public static void showCredentialDialog(Context context) {
    LayoutCredentialBinding binding = LayoutCredentialBinding.inflate(LayoutInflater.from(context));
    TextView helper = binding.helper;
    var content = helper.getText().toString();
    var linkText = context.getString(R.string.github_token);
    var startIndex = content.indexOf(linkText);
    var endIndex = startIndex + linkText.length();

    var spannableString = new SpannableString(content);
    spannableString.setSpan(
        new ClickableSpan() {
          @Override
          public void onClick(View textView) {
            String url = "https://github.com/settings/tokens";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);
          }
        },
        startIndex,
        endIndex,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    helper.setText(spannableString);
    helper.setMovementMethod(LinkMovementMethod.getInstance());
    helper.setHighlightColor(Color.TRANSPARENT);

    var prefs = PreferencesUtils.getDefaultPrefs();
    binding.etInputUsername.setText(
        prefs.getString(SharedPreferencesKeys.KEY_CREDENTIAL_USERNAME, ""));
    binding.etInputPassword.setText(
        prefs.getString(SharedPreferencesKeys.KEY_CREDENTIAL_PASSWORD, ""));

    new MaterialAlertDialogBuilder(context)
        .setTitle(R.string.pref_git_credentials)
        .setPositiveButton(
            android.R.string.ok,
            (d, w) -> {
              prefs
                  .edit()
                  .putString(
                      SharedPreferencesKeys.KEY_CREDENTIAL_USERNAME,
                      binding.etInputUsername.getText().toString())
                  .apply();
              prefs
                  .edit()
                  .putString(
                      SharedPreferencesKeys.KEY_CREDENTIAL_PASSWORD,
                      binding.etInputPassword.getText().toString())
                  .apply();
            })
        .setView(binding.getRoot())
        .show();
  }
}
