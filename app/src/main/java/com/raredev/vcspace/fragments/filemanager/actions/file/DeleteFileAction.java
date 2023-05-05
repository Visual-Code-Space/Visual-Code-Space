package com.raredev.vcspace.fragments.filemanager.actions.file;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.raredev.vcspace.R;
import com.raredev.vcspace.activity.MainActivity;
import com.raredev.vcspace.fragments.filemanager.FileManagerFragment;
import com.raredev.vcspace.fragments.filemanager.actions.FileBaseAction;
import com.raredev.vcspace.fragments.filemanager.adapters.FileAdapter;
import com.raredev.vcspace.task.TaskExecutor;
import com.raredev.vcspace.util.DialogUtils;
import com.raredev.vcspace.util.FileUtil;
import com.raredev.vcspace.util.ToastUtils;
import com.vcspace.actions.ActionData;
import java.io.File;

public class DeleteFileAction extends FileBaseAction {

  @Override
  public boolean isApplicable(File file, ActionData data) {
    return true;
  }

  @Override
  public void performAction(@NonNull ActionData data) {
    FileManagerFragment fragment = getFragment(data);
    FileAdapter adapter = getAdapter(data);
    File file = getFile(data);

    new MaterialAlertDialogBuilder(fragment.requireActivity())
        .setTitle(R.string.delete)
        .setMessage(fragment.getString(R.string.delete_message, file.getName()))
        .setPositiveButton(
            R.string.delete,
            (di, witch) -> {
              var progress =
                  DialogUtils.newProgressDialog(
                          fragment.requireContext(),
                          fragment.getString(R.string.deleting),
                          fragment.getString(R.string.deleting_please_wait))
                      .create();
              progress.setCancelable(false);
              progress.show();
              TaskExecutor.executeAsyncProvideError(
                  () -> {
                    return FileUtil.delete(file);
                  },
                  (result, error) -> {
                    progress.cancel();
                    if (result == null || error != null) {
                      return;
                    }
                    ((MainActivity) fragment.requireActivity()).onFileDeleted();
                    if (result) {
                      ToastUtils.showShort(
                          fragment.getString(R.string.deleted_message),
                          ToastUtils.TYPE_SUCCESS);
                    }

                    fragment.refreshFiles();
                  });
            })
        .setNegativeButton(R.string.cancel, (di, witch) -> di.dismiss())
        .show();
  }

  @Override
  public String getActionId() {
    return "delete.file.action";
  }

  @Override
  public String getTitle(Context context) {
    return context.getString(R.string.delete);
  }

  @Override
  public int getIcon() {
    return R.drawable.delete_outline;
  }
}
