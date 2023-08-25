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
import androidx.appcompat.app.AppCompatDelegate;
import com.raredev.vcspace.BaseApp;

public class Utils {

  public static int pxToDp(int value) {
    return (int)
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value,
            BaseApp.getInstance().getResources().getDisplayMetrics());
  }

  public static void updateImageTint(ImageView image, int color) {
    Drawable drawable = image.getDrawable();
    ColorFilter colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
    drawable.setTintList(ColorStateList.valueOf(color));
    drawable.setColorFilter(colorFilter);
  }

  public static boolean isDarkMode() {
    if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) return true;
    else if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO)
      return false;
    
    int uiMode =
        BaseApp.getInstance().getResources().getConfiguration().uiMode
            & Configuration.UI_MODE_NIGHT_MASK;
    return uiMode == Configuration.UI_MODE_NIGHT_YES;
  }
}
