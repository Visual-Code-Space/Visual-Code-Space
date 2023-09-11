package com.raredev.vcspace.utils;

import android.content.Context;
import android.view.LayoutInflater;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.raredev.vcspace.progressdialog.ProgressDialog;

public class DialogUtils {

  public static ProgressDialog newProgressDialog(Context context, String title, String message) {
    return ProgressDialog.create(context).setTitle(title).setLoadingMessage(message);
  }

  public static void newErrorDialog(Context context, String title, String message) {
    new MaterialAlertDialogBuilder(context)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(android.R.string.ok, (di, which) -> {})
        .show();
  }
}
