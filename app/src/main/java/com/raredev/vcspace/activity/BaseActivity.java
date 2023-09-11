package com.raredev.vcspace.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.elevation.SurfaceColors;
import com.raredev.vcspace.res.R;
import com.raredev.vcspace.util.Utils;

public abstract class BaseActivity extends AppCompatActivity {

  protected abstract View getLayout();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));
    getWindow().setNavigationBarColor(SurfaceColors.SURFACE_0.getColor(this));
    setContentView(getLayout());

    if (!Utils.isPermissionGaranted(this)) showRequestPermissionDialog();
  }

  private void showRequestPermissionDialog() {
    new MaterialAlertDialogBuilder(this)
        .setCancelable(false)
        .setTitle(R.string.file_access_title)
        .setMessage(R.string.file_access_message)
        .setPositiveButton(
            R.string.grant_permission,
            (d, w) -> {
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
              } else {
                ActivityCompat.requestPermissions(
                    this,
                    new String[] {
                      Manifest.permission.READ_EXTERNAL_STORAGE,
                      Manifest.permission.MANAGE_EXTERNAL_STORAGE
                    },
                    1);
              }
              d.cancel();
            })
        .setNegativeButton(
            R.string.exit,
            (d, w) -> {
              finishAffinity();
              System.exit(0);
            })
        .show();
  }
}
