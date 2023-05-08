package com.raredev.vcspace.fragments.filemanager.actions.file;

import android.content.Context;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import com.blankj.utilcode.util.ThreadUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.raredev.vcspace.R;
import com.raredev.vcspace.activity.MainActivity;
import com.raredev.vcspace.fragments.filemanager.FileManagerFragment;
import com.raredev.vcspace.fragments.filemanager.actions.FileBaseAction;
import com.raredev.vcspace.fragments.filemanager.adapters.FileAdapter;
import com.raredev.vcspace.progressdialog.ProgressDialog;
import com.raredev.vcspace.task.TaskExecutor;
import com.raredev.vcspace.util.DialogUtils;
import com.raredev.vcspace.util.ToastUtils;
import com.vcspace.actions.ActionData;
import java.io.File;

public class DeleteFileAction extends FileBaseAction {

  @Override
  public boolean isApplicable(File file, ActionData data) {
    return true;
  }

  @Override
  public void performAction(@NonNull ActionData data, MenuItem item) {
    FileManagerFragment fragment = getFragment(data);
    FileAdapter adapter = getAdapter(data);
    File file = getFile(data);

    new MaterialAlertDialogBuilder(fragment.requireActivity())
        .setTitle(R.string.delete)
        .setMessage(fragment.getString(R.string.delete_message, file.getName()))
        .setPositiveButton(
            R.string.delete,
            (di, witch) -> {
              ProgressDialog progress =
                  DialogUtils.newProgressDialog(
                      fragment.requireContext(),
                      fragment.getString(R.string.deleting),
                      fragment.getString(R.string.deleting_please_wait));

              AlertDialog dialog = progress.create();
              dialog.setCancelable(false);
              dialog.show();

              TaskExecutor.executeAsyncProvideError(
                  () -> {
                    return deleteFiles(
                        file,
                        message ->
                            ThreadUtils.runOnUiThread(() -> progress.setLoadingMessage(message)));
                  },
                  (result, error) -> {
                    dialog.cancel();
                    ((MainActivity) fragment.requireActivity()).onFileDeleted();
                    if (result) {
                      ToastUtils.showShort(
                          fragment.getString(R.string.deleted_message), ToastUtils.TYPE_SUCCESS);
                    }

                    fragment.refreshFiles();
                  });
            })
        .setNegativeButton(R.string.cancel, (di, witch) -> di.dismiss())
        .show();
  }

  private boolean deleteFiles(File file, @NonNull final UpdateListener listener) {
    if (!file.exists()) return false;

    listener.onUpdate("Deleting " + file.getName());

    if (file.isFile()) {
      if (file.delete()) {
        listener.onUpdate(file.getName() + " deleted!");
        return true;
      }
      return false;
    }

    File[] fileArr = file.listFiles();

    if (fileArr != null) {
      for (File subFile : fileArr) {
        if (subFile.isDirectory()) {
          if (!deleteFiles(subFile, listener)) {
            return false; // Returns false if deletion fails in any subdirectories
          }
        }

        if (subFile.isFile()) {
          if (!subFile.delete()) {
            return false; // Returns false if deletion fails on any file
          }
          listener.onUpdate(subFile.getName() + " deleted!");
        }
      }
    }

    if (file.delete()) {
      listener.onUpdate(file.getName() + " deleted!");
      return true;
    }
    return false;
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

  public interface UpdateListener {
    void onUpdate(String message);
  }
}
