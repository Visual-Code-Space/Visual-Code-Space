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
import java.io.IOException;

public class CreateFileAction extends FileTreeBaseAction {

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
    binding.tvInputLayout.setHint(fragment.getString(R.string.file_name_hint));

    new MaterialAlertDialogBuilder(fragment.requireActivity())
        .setTitle(R.string.new_file_title)
        .setPositiveButton(
            R.string.create,
            (di, witch) -> {
              try {
                File newFile =
                    new File(node.getValue(), "/" + et_filename.getText().toString().trim());
                if (!newFile.exists()) {
                  if (newFile.createNewFile()) {
                    fragment.addNewChild(node, newFile);
                    fragment.expandNode(node);
                  }
                }
              } catch (IOException ioe) {
              }
            })
        .setNegativeButton(R.string.cancel, (di, witch) -> di.dismiss())
        .setView(binding.getRoot())
        .show();
  }

  @Override
  public int getIcon() {
    return R.drawable.file_plus_outline;
  }

  @Override
  public int getTitle() {
    return R.string.new_file_title;
  }
}
