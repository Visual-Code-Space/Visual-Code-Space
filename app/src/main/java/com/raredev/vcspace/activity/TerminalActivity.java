package com.raredev.vcspace.activity;

import android.view.View;
import com.raredev.vcspace.databinding.ActivityTerminalBinding;

public class TerminalActivity extends VCSpaceActivity {
  private ActivityTerminalBinding binding;

  @Override
  public View getLayout() {
    binding = ActivityTerminalBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  public void onCreate() {
    
  }
}
