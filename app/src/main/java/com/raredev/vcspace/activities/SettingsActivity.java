package com.raredev.vcspace.activities;

import static com.raredev.vcspace.res.R.string;

import android.os.Bundle;
import android.view.View;
import com.raredev.vcspace.R;
import com.raredev.vcspace.databinding.ActivitySettingsBinding;
import com.raredev.vcspace.fragments.SettingsFragment;

public class SettingsActivity extends BaseActivity {
  private ActivitySettingsBinding binding;

  @Override
  protected View getLayout() {
    binding = ActivitySettingsBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setSupportActionBar(binding.toolbar);
    getSupportActionBar().setTitle(string.menu_settings);
    binding.toolbar.setNavigationOnClickListener((v) -> onBackPressed());

    if (getSupportFragmentManager().findFragmentByTag(SettingsFragment.TAG) == null) {
      getSupportFragmentManager()
          .beginTransaction()
          .add(R.id.settings_container, new SettingsFragment(), SettingsFragment.TAG)
          .commit();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    binding = null;
  }
}
