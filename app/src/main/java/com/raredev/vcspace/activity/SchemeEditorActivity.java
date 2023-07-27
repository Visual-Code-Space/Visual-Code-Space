package com.raredev.vcspace.activity;

import android.os.Bundle;
import android.view.View;
import com.raredev.vcspace.databinding.ActivitySchemeEditorBinding;

public class SchemeEditorActivity extends BaseActivity {

  private ActivitySchemeEditorBinding binding;

  @Override
  public View getLayout() {
    binding = ActivitySchemeEditorBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
  }
}
