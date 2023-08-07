package com.raredev.vcspace.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.raredev.vcspace.databinding.ActivityChooseFolderBinding;

public class FileExplorerActivity extends BaseActivity {

  public static final int RESULT_CODE = 0;

  private ActivityChooseFolderBinding binding;

  public static void startPickPathActivity(Activity act) {
    var it = new Intent(act, FileExplorerActivity.class);
    act.startActivityForResult(it, RESULT_CODE);
  }

  @Override
  public View getLayout() {
    binding = ActivityChooseFolderBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setSupportActionBar(binding.toolbar);
    binding.toolbar.setNavigationOnClickListener((v) -> onBackPressed());
    
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    binding = null;
  }
}
