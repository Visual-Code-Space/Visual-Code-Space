package com.raredev.vcspace.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.raredev.vcspace.databinding.ActivityCrashBinding;
import com.raredev.vcspace.res.R;
import java.util.Calendar;
import java.util.Date;

public class CrashActivity extends BaseActivity {
  public static final String KEY_EXTRA_ERROR = "error";

  private static final String newLine = "\n";

  private ActivityCrashBinding binding;

  @Override
  protected View getLayout() {
    binding = ActivityCrashBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityCrashBinding.inflate(getLayoutInflater());

    setContentView(binding.getRoot());
    setSupportActionBar(binding.topAppBar);
    getSupportActionBar().setTitle("VCSpace Crash");

    var error = new StringBuilder();

    error.append("Manufacturer: " + DeviceUtils.getManufacturer() + "\n");
    error.append("Device: " + DeviceUtils.getModel() + "\n");
    error.append(getSoftwareInfo());
    error.append("\n\n");
    error.append(getIntent().getStringExtra(KEY_EXTRA_ERROR));
    error.append("\n\n");
    error.append(getDate());
    error.append("\n\n");

    binding.result.setText(error.toString());

    binding.fab.setOnClickListener(
        v -> {
          ClipboardUtils.copyText(binding.result.getText());
        });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    var close = menu.add(getString(R.string.close));
    close.setContentDescription("Close App");
    close.setIcon(R.drawable.close);
    close.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getTitle().equals(getString(R.string.close))) {
      onBackPressed();
      return true;
    }
    return false;
  }

  @Override
  public void onBackPressed() {
    finishAffinity();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    binding = null;
  }

  private String getSoftwareInfo() {
    return new StringBuilder("SDK: ")
        .append(Build.VERSION.SDK_INT)
        .append(newLine)
        .append("Android: ")
        .append(Build.VERSION.RELEASE)
        .append(newLine)
        .append("Model: ")
        .append(Build.VERSION.INCREMENTAL)
        .append(newLine)
        .toString();
  }

  private Date getDate() {
    return Calendar.getInstance().getTime();
  }
}
