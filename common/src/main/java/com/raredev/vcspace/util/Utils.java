package com.raredev.vcspace.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.util.TypedValue;

public class Utils {

  private static Context mContext;

  public static void init(Context context) {
    mContext = context;
  }

  public static Context getContext() {
    return mContext;
  }

  public static int pxToDp(int value) {
    return (int)
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, value, mContext.getResources().getDisplayMetrics());
  }

  public static void copyText(final CharSequence text) {
    ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
    cm.setPrimaryClip(ClipData.newPlainText(mContext.getPackageName(), text));
  }

  public static boolean isDarkMode() {
    int uiMode =
        mContext.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    return uiMode == Configuration.UI_MODE_NIGHT_YES;
  }
}
