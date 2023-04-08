package com.raredev.vcspace.actions.main.filetree;

import android.view.LayoutInflater;
import android.widget.EditText;
import androidx.annotation.NonNull;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.main.FileTreeBaseAction;
import com.raredev.vcspace.databinding.LayoutTextinputBinding;
import com.raredev.vcspace.fragments.FileTreeFragment;
import com.unnamed.b.atv.model.TreeNode;
import com.vcspace.actions.ActionData;
import java.io.File;

public class RenameFileAction extends FileTreeBaseAction {

  @Override
  public boolean isApplicable(File file) {
    return true;
  }

  @Override
  public void performAction(@NonNull ActionData data) {
    FileTreeFragment fragment = getFragment(data);
    TreeNode node = getNode(data);

    LayoutTextinputBinding binding =
        LayoutTextinputBinding.inflate(LayoutInflater.from(fragment.requireActivity()));
    EditText et_filename = binding.etInput;
    binding.tvInputLayout.setHint(R.string.rename_hint);

    et_filename.setText(node.getValue().getName());

    new MaterialAlertDialogBuilder(fragment.requireActivity())
        .setTitle(R.string.rename)
        .setPositiveButton(
            R.string.rename,
            (di, witch) -> {
              File newFile = new File(node.getValue().getAbsolutePath());

              if (!et_filename.getText().toString().equals(node.getValue().getName())) {
                if (newFile.renameTo(
                    new File(
                        node.getValue().getParentFile(),
                        et_filename.getText().toString().trim()))) {
                  TreeNode parent = node.getParent();
                  if (parent != null) {}

                  fragment.updateRootNode(newFile);
                }
              }
            })
        .setNegativeButton(R.string.cancel, (di, witch) -> di.dismiss())
        .setView(binding.getRoot())
        .show();
  }

  @Override
  public int getIcon() {
    return R.drawable.file_rename;
  }

  @Override
  public int getTitle() {
    return R.string.rename;
  }
}
