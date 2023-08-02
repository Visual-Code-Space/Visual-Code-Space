package com.raredev.vcspace.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.elevation.SurfaceColors;
import com.raredev.vcspace.CrashHandler;
import com.raredev.vcspace.R;
import com.raredev.vcspace.util.Utils;

public abstract class BaseActivity extends AppCompatActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));
    getWindow().setNavigationBarColor(SurfaceColors.SURFACE_0.getColor(this));
    Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
    setContentView(getLayout());
    Utils.init(this);

    if (!isPermissionGaranted(this)) {
      takeFilePermissions(this);
    }
  }

  public abstract View getLayout();

  public static void takeFilePermissions(Activity activity) {
    new MaterialAlertDialogBuilder(activity)
        .setCancelable(false)
        .setTitle(R.string.file_access_title)
        .setMessage(R.string.file_access_message)
        .setPositiveButton(
            R.string.grant_permission,
            (d, w) -> {
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivity(intent);
              } else {
                ActivityCompat.requestPermissions(
                    activity,
                    new String[] {
                      Manifest.permission.READ_EXTERNAL_STORAGE,
                      Manifest.permission.MANAGE_EXTERNAL_STORAGE
                    },
                    1);
              }
            })
        .setNegativeButton(
            R.string.exit,
            (d, w) -> {
              activity.finish();
              System.exit(0);
            })
        .show();
  }

  public static boolean isPermissionGaranted(Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      return Environment.isExternalStorageManager();
    } else {
      return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
          == PackageManager.PERMISSION_GRANTED;
    }
  }
}
