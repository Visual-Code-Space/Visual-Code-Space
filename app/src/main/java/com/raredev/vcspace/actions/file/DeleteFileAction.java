package com.raredev.vcspace.actions.file;

import androidx.appcompat.app.AlertDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.raredev.common.task.TaskExecutor;
import com.raredev.common.util.DialogUtils;
import com.raredev.common.util.FileUtil;
import com.raredev.vcspace.R;
import com.raredev.vcspace.activity.MainActivity;
import com.raredev.vcspace.fragments.TreeViewFragment;
import com.unnamed.b.atv.model.TreeNode;
import java.io.File; 

public class DeleteFileAction extends FileAction {

  @Override
  public boolean isApplicable(File file) {
    return true;
  }

  @Override
  public void performAction() {
    TreeViewFragment fragment = (TreeViewFragment) getActionEvent().getData("fragment");
    TreeNode node = (TreeNode) getActionEvent().getData("node");

    new MaterialAlertDialogBuilder(fragment.requireActivity())
        .setTitle(R.string.delete)
        .setMessage(fragment.getString(R.string.delete_message, node.getValue().getName()))
        .setPositiveButton(
            R.string.delete,
            (dlg, i) -> {
              AlertDialog progress =
                  DialogUtils.newProgressDialog(
                          fragment.requireContext(),
                          fragment.getString(R.string.deleting),
                          fragment.getString(R.string.deleting_plase_wait))
                      .create();
              progress.setCancelable(false);
              progress.show();
              TaskExecutor.executeAsyncProvideError(
                  () -> {
                    return FileUtil.delete(node.getValue().getAbsolutePath());
                  },
                  (result, error) -> {
                    ((MainActivity) fragment.requireActivity()).editorManager.onFileDeleted();
                    if (node != null) {
                      fragment.getTreeView().removeNode(node);
                    }
                    progress.cancel();
                    fragment.observeRoot();
                  });
            })
        .setNegativeButton(R.string.cancel, null)
        .show();
  }

  @Override
  public int getIcon() {
    return R.drawable.delete_outline;
  }

  @Override
  public int getTitle() {
    return R.string.delete;
  }
}
