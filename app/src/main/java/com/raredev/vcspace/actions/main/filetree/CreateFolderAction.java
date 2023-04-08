package com.raredev.vcspace.actions.main.filetree;

import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.main.FileTreeBaseAction;
import com.raredev.vcspace.databinding.LayoutTextinputBinding;
import com.raredev.vcspace.fragments.FileTreeFragment;
import com.unnamed.b.atv.model.TreeNode;
import com.vcspace.actions.ActionData;
import java.io.File;

public class CreateFolderAction extends FileTreeBaseAction {

  @Override
  public boolean isApplicable(File file) {
    return file.isDirectory();
  }

  @Override
  public void performAction(@NonNull ActionData data) {
    FileTreeFragment fragment = getFragment(data);
    TreeNode node = getNode(data);

    LayoutTextinputBinding binding =
        LayoutTextinputBinding.inflate(LayoutInflater.from(fragment.requireActivity()));
    TextInputEditText et_filename = binding.etInput;
    binding.tvInputLayout.setHint(fragment.getString(R.string.folder_name_hint));

    new MaterialAlertDialogBuilder(fragment.requireActivity())
        .setTitle(R.string.new_folder_title)
        .setPositiveButton(
            R.string.create,
            (di, witch) -> {
              File newFolder =
                  new File(node.getValue(), "/" + et_filename.getText().toString().trim());
              if (!newFolder.exists()) {
                if (newFolder.mkdirs()) {
                  fragment.addNewChild(node, newFolder);
                  fragment.expandNode(node);
                }
              }
            })
        .setNegativeButton(R.string.cancel, (di, witch) -> di.dismiss())
        .setView(binding.getRoot())
        .show();
  }

  @Override
  public int getIcon() {
    return R.drawable.folder_plus_outline;
  }

  @Override
  public int getTitle() {
    return R.string.new_folder_title;
  }
}
