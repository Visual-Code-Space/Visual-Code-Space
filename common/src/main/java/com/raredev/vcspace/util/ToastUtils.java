package com.raredev.vcspace.util;

import android.widget.Toast;
import com.raredev.vcspace.BaseApp;

public class ToastUtils {
  public static final int TYPE_ERROR = 0;
  public static final int TYPE_SUCCESS = 2;

  public static void showShort(CharSequence message, int type) {
    Toast.makeText(BaseApp.getInstance().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
  }

  public static void showLong(CharSequence message, int type) {
    Toast.makeText(BaseApp.getInstance().getApplicationContext(), message, Toast.LENGTH_LONG).show();
  }
}
