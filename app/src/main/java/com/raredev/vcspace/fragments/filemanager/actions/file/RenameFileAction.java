package com.raredev.vcspace.fragments.filemanager.actions.file;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import com.blankj.utilcode.util.KeyboardUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.raredev.vcspace.R;
import com.raredev.vcspace.adapters.FileListAdapter;
import com.raredev.vcspace.databinding.LayoutTextinputBinding;
import com.raredev.vcspace.fragments.filemanager.FileManagerFragment;
import com.raredev.vcspace.fragments.filemanager.actions.FileBaseAction;
import com.raredev.vcspace.util.ToastUtils;
import com.vcspace.actions.ActionData;
import java.io.File;

public class RenameFileAction extends FileBaseAction {

  @Override
  public boolean isApplicable(File file, ActionData data) {
    FileListAdapter adapter = getAdapter(data);
    return !adapter.isFilesSelected();
  }

  @Override
  public void performAction(@NonNull ActionData data) {
    FileManagerFragment fragment = getFragment(data);
    File file = getFile(data);

    LayoutTextinputBinding binding =
        LayoutTextinputBinding.inflate(LayoutInflater.from(fragment.requireActivity()));

    AlertDialog dialog =
        new MaterialAlertDialogBuilder(fragment.requireActivity())
        .setView(binding.getRoot())
            .setTitle(R.string.rename)
            .setPositiveButton(R.string.rename, null)
            .setNegativeButton(R.string.cancel, null)
            .create();
    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    dialog.setOnShowListener(
        (p1) -> {
          TextInputEditText et_filename = binding.etInput;
          Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);

          binding.tvInputLayout.setHint(R.string.rename_hint);

          String oldFileName = file.getName();

          et_filename.setText(oldFileName);
          int lastIndex = oldFileName.lastIndexOf(".");
          if (lastIndex >= 0) {
            et_filename.setSelection(0, lastIndex);
          }
          et_filename.requestFocus();

          positive.setOnClickListener(
              v -> {
                String newFileName = et_filename.getText().toString().trim();
                File newFile = new File(file.getParentFile(), newFileName);

                if (!newFileName.equals(oldFileName)) {
                  if (file.renameTo(newFile)) {
                    ToastUtils.showShort(
                        fragment.getString(
                            R.string.renamed_message, file.getName(), newFile.getName()),
                        ToastUtils.TYPE_SUCCESS);
                    fragment.refreshFiles();
                  }
                }
                dialog.dismiss();
              });
        });
    dialog.show();
  }

  @Override
  public String getActionId() {
    return "rename.file.action";
  }

  @Override
  public String getTitle(Context context) {
    return context.getString(R.string.rename);
  }

  @Override
  public int getIcon() {
    return R.drawable.file_rename;
  }
}
