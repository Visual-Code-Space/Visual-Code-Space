package com.raredev.vcspace.activity;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.elevation.SurfaceColors;
import com.raredev.vcspace.CrashHandler;
import com.raredev.vcspace.fragments.SettingsFragment;

public class VCSpaceActivity extends AppCompatActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AppCompatDelegate.setDefaultNightMode(SettingsFragment.getThemeFromPrefs());
    Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
    getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));
    findBinding();
    setContentView(getLayout());
    onCreate();
  }

  public View getLayout() {
    return null;
  }

  public void findBinding() {}

  public void onCreate() {}
}
