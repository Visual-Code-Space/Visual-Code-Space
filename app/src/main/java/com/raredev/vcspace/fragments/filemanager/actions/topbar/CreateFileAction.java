package com.raredev.vcspace.fragments.filemanager.actions.topbar;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.raredev.vcspace.R;
import com.raredev.vcspace.databinding.LayoutTextinputBinding;
import com.raredev.vcspace.fragments.filemanager.FileManagerFragment;
import com.raredev.vcspace.fragments.filemanager.actions.TopbarBaseAction;
import com.raredev.vcspace.util.ILogger;
import com.raredev.vcspace.util.ToastUtils;
import com.vcspace.actions.ActionData;
import java.io.File;
import java.io.IOException;

public class CreateFileAction extends TopbarBaseAction {

  @Override
  public void performAction(@NonNull ActionData data) {
    FileManagerFragment fragment = getFragment(data);
    File file = getFile(data);

    LayoutTextinputBinding binding =
        LayoutTextinputBinding.inflate(LayoutInflater.from(fragment.requireActivity()));

    AlertDialog dialog =
        new MaterialAlertDialogBuilder(fragment.requireActivity())
            .setView(binding.getRoot())
            .setTitle(R.string.new_file_title)
            .setPositiveButton(R.string.create, null)
            .setNegativeButton(R.string.cancel, null)
            .create();

    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    dialog.setOnShowListener(
        (p1) -> {
          TextInputEditText et_filename = binding.etInput;
          binding.tvInputLayout.setHint(fragment.getString(R.string.file_name_hint));
          Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);

          et_filename.requestFocus();
          positive.setOnClickListener(
              v -> {
                try {
                  File newFile = new File(file, et_filename.getText().toString());
                  if (!newFile.exists()) {
                    if (newFile.createNewFile()) {
                      fragment.refreshFiles();
                    }
                  } else {
                    ToastUtils.showShort(
                        fragment.getString(R.string.existing_file), ToastUtils.TYPE_ERROR);
                  }
                } catch (IOException ioe) {
                  ILogger.error(getActionId(), Log.getStackTraceString(ioe));
                }
                dialog.dismiss();
              });
        });
    dialog.show();
  }

  @Override
  public String getActionId() {
    return "create.file.action";
  }

  @Override
  public String getTitle(Context context) {
    return context.getString(R.string.new_file_title);
  }

  @Override
  public int getIcon() {
    return R.drawable.file_plus_outline;
  }
}
