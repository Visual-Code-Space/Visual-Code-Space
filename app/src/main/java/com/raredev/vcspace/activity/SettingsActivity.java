package com.raredev.vcspace.activity;

import android.os.Bundle;
import android.view.View;
import com.raredev.vcspace.R;
import com.raredev.vcspace.databinding.ActivitySettingsBinding;

public class SettingsActivity extends BaseActivity {
  private ActivitySettingsBinding binding;

  @Override
  public View getLayout() {
    binding = ActivitySettingsBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setSupportActionBar(binding.toolbar);
    getSupportActionBar().setTitle(R.string.menu_settings);
    binding.toolbar.setNavigationOnClickListener((v) -> onBackPressed());
  }
}
