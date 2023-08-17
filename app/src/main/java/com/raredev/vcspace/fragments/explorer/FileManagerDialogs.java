package com.raredev.vcspace.fragments.explorer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import com.blankj.utilcode.util.ThreadUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.raredev.vcspace.databinding.LayoutTextinputBinding;
import com.raredev.vcspace.progressdialog.ProgressDialog;
import com.raredev.vcspace.res.R;
import com.raredev.vcspace.task.TaskExecutor;
import com.raredev.vcspace.util.ApkInstaller;
import com.raredev.vcspace.util.DialogUtils;
import com.raredev.vcspace.util.FileUtil;
import com.raredev.vcspace.util.ToastUtils;
import java.io.File;

public class FileManagerDialogs {
  public static void createFile(Context context, File file, Concluded concluded) {
    createNew(context, file, false, concluded);
  }

  public static void createFolder(Context context, File file, Concluded concluded) {
    createNew(context, file, true, concluded);
  }

  @SuppressWarnings("deprecation")
  private static void createNew(Context context, File file, boolean isFolder, Concluded concluded) {
    LayoutTextinputBinding binding = LayoutTextinputBinding.inflate(LayoutInflater.from(context));

    AlertDialog dialog =
        new MaterialAlertDialogBuilder(context)
            .setView(binding.getRoot())
            .setTitle(isFolder ? R.string.new_folder_title : R.string.new_file_title)
            .setPositiveButton(R.string.create, null)
            .setNegativeButton(R.string.cancel, null)
            .create();

    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

    dialog.setOnShowListener(
        (p1) -> {
          TextInputEditText et_filename = binding.etInput;
          binding.tvInputLayout.setHint(
              isFolder
                  ? context.getString(R.string.folder_name_hint)
                  : context.getString(R.string.file_name_hint));
          et_filename.requestFocus();
          Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
          positive.setOnClickListener(
              v -> {
                if (isFolder) {
                  File newFolder = new File(file, "/" + et_filename.getText().toString());
                  if (!newFolder.exists()) {
                    if (newFolder.mkdirs()) {
                      concluded.concluded(newFolder);
                    }
                  }
                } else {
                  File newFile = new File(file, "/" + et_filename.getText().toString());
                  if (!newFile.exists()) {
                    if (FileUtil.writeFile(newFile.getAbsolutePath(), "")) {
                      concluded.concluded(newFile);
                    }
                  }
                }
                dialog.dismiss();
              });
        });

    dialog.show();
  }

  public static void renameFile(Context context, File file, OnFileRenamed onFileRenamed) {
    LayoutTextinputBinding binding = LayoutTextinputBinding.inflate(LayoutInflater.from(context));

    AlertDialog dialog =
        new MaterialAlertDialogBuilder(context)
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
                        context.getString(R.string.renamed_message, oldFileName, newFile.getName()),
                        ToastUtils.TYPE_SUCCESS);
                    onFileRenamed.onFileRenamed(file, newFile);
                  }
                }
                dialog.dismiss();
              });
        });
    dialog.show();
  }

  public static void deleteFile(Context context, File file, Concluded concluded) {
    new MaterialAlertDialogBuilder(context)
        .setTitle(R.string.delete)
        .setMessage(context.getString(R.string.delete_message, file.getName()))
        .setPositiveButton(
            R.string.delete,
            (di, witch) -> {
              ProgressDialog progress =
                  DialogUtils.newProgressDialog(
                      context,
                      context.getString(R.string.deleting),
                      context.getString(R.string.deleting_please_wait));

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
                    if (result) {
                      ToastUtils.showShort(
                          context.getString(R.string.deleted_message), ToastUtils.TYPE_SUCCESS);

                      concluded.concluded(file);
                    }
                  });
            })
        .setNegativeButton(R.string.cancel, (di, witch) -> di.dismiss())
        .show();
  }

  private static boolean deleteFiles(File file, @NonNull final UpdateListener listener) {
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

  public static void showApkInfoDialog(Context context, File file) {
    var builder = new MaterialAlertDialogBuilder(context);

    builder.setTitle("Install apk");

    PackageManager pm = context.getPackageManager();

    PackageInfo pi = pm.getPackageArchiveInfo(file.getAbsolutePath(), 0);
    if (pi != null) {
      builder.setIcon(pi.applicationInfo.loadIcon(pm));
      builder.setTitle(pi.applicationInfo.loadLabel(pm));
      builder.setMessage(
          "Package: "
              + pi.packageName
              + "\nVersion code: "
              + pi.versionCode
              + "\nVersion name: "
              + pi.versionName);
    } else {
      builder.setMessage("No apk info.");
    }
    builder.setNegativeButton(R.string.cancel, null);
    builder.setPositiveButton(
        "Install",
        (di, witch) -> {
          ApkInstaller.installApplication(context, file);
        });
    builder.show();
  }

  public interface UpdateListener {
    void onUpdate(String message);
  }

  public interface OnFileRenamed {
    void onFileRenamed(File oldFile, File newFile);
  }

  public interface Concluded {
    void concluded(File file);
  }
}
