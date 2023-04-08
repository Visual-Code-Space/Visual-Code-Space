package com.raredev.vcspace.activity;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.elevation.SurfaceColors;
import com.raredev.vcspace.CrashHandler;
import com.raredev.vcspace.fragments.SettingsFragment;

public abstract class VCSpaceActivity extends AppCompatActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AppCompatDelegate.setDefaultNightMode(SettingsFragment.getThemeFromPrefs());
    getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));
    Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
    setContentView(getLayout());
    onCreate();
  }

  public abstract View getLayout();

  public abstract void onCreate();
}
