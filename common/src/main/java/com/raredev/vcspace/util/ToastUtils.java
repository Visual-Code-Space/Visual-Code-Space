package com.raredev.vcspace.util;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.color.MaterialColors;

public class ToastUtils {
  public static final int TYPE_ERROR = 0;
  public static final int TYPE_SUCCESS = 2;

  public static void showShort(CharSequence message, int type) {
    Toast toast = new Toast(Utils.getContext());
    toast.setView(createToastView(message, type));
    toast.setDuration(Toast.LENGTH_SHORT);
    toast.show();
  }

  public static void showLong(CharSequence message, int type) {
    Toast toast = new Toast(Utils.getContext());
    toast.setView(createToastView(message, type));
    toast.setDuration(Toast.LENGTH_LONG);
    toast.show();
  }

  private static View createToastView(CharSequence message, int type) {
    TextView tv = new TextView(Utils.getContext());
    int padding = Utils.pxToDp(12);
    tv.setPadding(padding * 1, padding, padding * 1, padding);
    tv.setTextColor(
        MaterialColors.getColor(
            Utils.getContext(), com.google.android.material.R.attr.colorOnSurface, 0));
    tv.setText(message);

    GradientDrawable drawable = new GradientDrawable();
    drawable.setShape(GradientDrawable.RECTANGLE);
    drawable.setCornerRadius(Utils.pxToDp(15));
    drawable.setColor(
        MaterialColors.getColor(
            Utils.getContext(), com.google.android.material.R.attr.colorSurface, 0));
    drawable.setStroke(1, getColorForType(type));

    tv.setBackground(drawable);
    return tv;
  }

  private static int getColorForType(int type) {
    switch (type) {
      case TYPE_ERROR:
        return Color.parseColor("#64FF0B0B");
      case TYPE_SUCCESS:
        return Color.parseColor("#640BFF0B");
      default:
        return 0;
    }
  }
}
