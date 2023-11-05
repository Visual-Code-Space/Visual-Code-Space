package com.raredev.vcspace.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Build;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.raredev.vcspace.app.BaseApplication;
import java.io.IOException;

public class Utils {

  public static void setDrawableTint(Drawable drawable, int color) {
    drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
  }

  public static void setActivityTitle(Activity activity, String title) {
    if (activity instanceof AppCompatActivity) {
      ((AppCompatActivity) activity).getSupportActionBar().setTitle(title);
    } else {
      activity.getActionBar().setTitle(title);
    }
  }

  public static boolean isPermissionGaranted(Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      return Environment.isExternalStorageManager();
    } else {
      return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
          == PackageManager.PERMISSION_GRANTED;
    }
  }

  public static boolean isDarkMode() {
    if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) return true;
    else if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO)
      return false;

    int uiMode =
        BaseApplication.Companion.getInstance().getResources().getConfiguration().uiMode
            & Configuration.UI_MODE_NIGHT_MASK;
    return uiMode == Configuration.UI_MODE_NIGHT_YES;
  }

  public static Drawable getDrawableFromSvg(String assetsPath) {
    try {
      var svg = SVG.getFromAsset(BaseApplication.Companion.getInstance().getAssets(), assetsPath);
      return new PictureDrawable(svg.renderToPicture());
    } catch (SVGParseException | IOException err) {
      err.printStackTrace();
      return null;
    }
  }
}
