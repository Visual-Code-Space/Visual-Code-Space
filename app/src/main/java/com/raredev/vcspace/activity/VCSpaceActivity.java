package com.raredev.vcspace.activity;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.raredev.vcspace.CrashHandler;

public class VCSpaceActivity extends AppCompatActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
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
