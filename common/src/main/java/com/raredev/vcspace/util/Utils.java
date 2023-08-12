package com.raredev.vcspace.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.widget.ImageView;

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

  public static void updateImageTint(ImageView image, int color) {
    Drawable drawable = image.getDrawable();
    ColorFilter colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
    drawable.setTintList(ColorStateList.valueOf(color));
    drawable.setColorFilter(colorFilter);
  }

  public static boolean isDarkMode() {
    int uiMode =
        mContext.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    return uiMode == Configuration.UI_MODE_NIGHT_YES;
  }
}
