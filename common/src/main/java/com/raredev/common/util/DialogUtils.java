package com.raredev.common.util;

import android.content.Context;
import android.view.LayoutInflater;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.raredev.common.databinding.LayoutProgressDialogBinding;

public class DialogUtils {

  public static MaterialAlertDialogBuilder newProgressDialog(
      Context context, String title, String message) {
    LayoutProgressDialogBinding binding =
        LayoutProgressDialogBinding.inflate(LayoutInflater.from(context));
    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

    builder.setTitle(title);
    binding.message.setText(message);
    builder.setView(binding.getRoot());
    return builder;
  }

  public static void newErrorDialog(Context context, String title, String message) {
    new MaterialAlertDialogBuilder(context)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(android.R.string.ok, (di, which) -> {})
        .show();
  }
}
