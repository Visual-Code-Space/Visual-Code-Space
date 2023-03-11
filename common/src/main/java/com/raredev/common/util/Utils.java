package com.raredev.common.util;

import android.content.Context;
import android.util.TypedValue;

public class Utils {
  public static int pxToDp(Context ctx, int value) {
    return (int) TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value, ctx.getResources().getDisplayMetrics());
  }
}
