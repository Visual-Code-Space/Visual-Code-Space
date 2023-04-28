package com.raredev.vcspace.fragments.filemanager.actions.file;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.EditText;
import androidx.annotation.NonNull;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
    EditText et_filename = binding.etInput;
    binding.tvInputLayout.setHint(R.string.rename_hint);

    et_filename.setText(file.getName());

    new MaterialAlertDialogBuilder(fragment.requireActivity())
        .setTitle(R.string.rename)
        .setPositiveButton(
            R.string.rename,
            (di, witch) -> {
              File newFile =
                  new File(file.getParentFile(), et_filename.getText().toString().trim());

              if (!et_filename.getText().toString().equals(file.getName())) {
                if (file.renameTo(newFile)) {
                  ToastUtils.showShort(
                      fragment.getString(
                          R.string.renamed_message, file.getName(), newFile.getName()),
                      ToastUtils.TYPE_SUCCESS);
                  fragment.refreshFiles();
                }
              }
            })
        .setNegativeButton(R.string.cancel, (di, witch) -> di.dismiss())
        .setView(binding.getRoot())
        .show();
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
