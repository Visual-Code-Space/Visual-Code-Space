package com.raredev.vcspace.activity;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.elevation.SurfaceColors;
import com.raredev.vcspace.CrashHandler;

public abstract class VCSpaceActivity extends AppCompatActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());
    getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));
    setContentView(getLayout());
    onCreate();
  }

  public abstract View getLayout();

  public abstract void onCreate();
}
