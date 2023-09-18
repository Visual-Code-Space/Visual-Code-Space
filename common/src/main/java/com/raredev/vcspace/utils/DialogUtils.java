package com.raredev.vcspace.utils;

import android.content.Context;
import com.raredev.vcspace.progressdialog.ProgressDialog;

public class DialogUtils {

  public static ProgressDialog newProgressDialog(Context context, String title, String message) {
    return ProgressDialog.create(context).setTitle(title).setLoadingMessage(message);
  }
}
