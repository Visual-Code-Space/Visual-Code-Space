package com.raredev.vcspace.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.raredev.common.task.TaskExecutor;
import com.raredev.common.util.DialogUtils;
import com.raredev.common.util.FileUtil;
import com.raredev.vcspace.R;
import com.raredev.vcspace.databinding.LayoutTextinputBinding;
import java.io.File;
import java.util.Comparator;

public class FileManagerUtils {

  public static class SortFileName implements Comparator<File> {
    @Override
    public int compare(File f1, File f2) {
      return f1.getName().compareTo(f2.getName());
    }
  }

  public static class SortFolder implements Comparator<File> {
    @Override
    public int compare(File f1, File f2) {
      if (f1.isDirectory() == f2.isDirectory()) return 0;
      else if (f1.isDirectory() && !f2.isDirectory()) return -1;
      else return 1;
    }
  }

  public static boolean isValidTextFile(String filename) {
    return !filename.matches(
        ".*\\.(bin|ttf|png|jpe?g|bmp|mp4|mp3|m4a|iso|so|zip|jar|dex|odex|vdex|7z|apk|apks|xapk)$");
  }

  public static void createFile(Context context, File file, Concluded concluded) {
    createNew(context, file, concluded, false);
  }

  public static void createFolder(Context context, File file, Concluded concluded) {
    createNew(context, file, concluded, true);
  }

  @SuppressWarnings("deprecation")
  private static void createNew(Context context, File file, Concluded concluded, boolean isFolder) {
    LayoutInflater inflater = LayoutInflater.from(context);
    LayoutTextinputBinding binding = LayoutTextinputBinding.inflate(inflater);
    EditText et_filename = binding.etInput;
    binding.tvInputLayout.setHint(
        isFolder
            ? context.getString(R.string.folder_name_hint)
            : context.getString(R.string.file_name_hint));

    new MaterialAlertDialogBuilder(context)
        .setTitle(isFolder ? R.string.new_folder_title : R.string.new_file_title)
        .setPositiveButton(
            R.string.create,
            (dlg, i) -> {
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
            })
        .setNegativeButton(R.string.cancel, null)
        .setView(binding.getRoot())
        .show();
  }

  public static void renameFile(Context context, File file, OnFileRenamed onFileRenamed) {
    LayoutInflater inflater = LayoutInflater.from(context);
    LayoutTextinputBinding binding = LayoutTextinputBinding.inflate(inflater);
    EditText et_filename = binding.etInput;
    binding.tvInputLayout.setHint(R.string.rename_hint);

    et_filename.setText(file.getName());

    new MaterialAlertDialogBuilder(context)
        .setTitle(R.string.rename)
        .setPositiveButton(
            R.string.rename,
            (dlg, i) -> {
              File newFile = new File(file.getAbsolutePath());

              if (newFile.exists()) {
                if (newFile.renameTo(
                    new File(file.getParentFile(), et_filename.getText().toString()))) {
                  onFileRenamed.onFileRenamed(file, newFile);
                }
              }
            })
        .setNegativeButton(R.string.cancel, null)
        .setView(binding.getRoot())
        .show();
  }

  public static void deleteFile(Context context, File file, Concluded concluded) {
    new MaterialAlertDialogBuilder(context)
        .setTitle(R.string.delete)
        .setMessage(context.getString(R.string.delete_message, file.getName()))
        .setPositiveButton(
            R.string.delete,
            (dlg, i) -> {
              AlertDialog progress =
                  DialogUtils.newProgressDialog(
                          context,
                          context.getString(R.string.deleting),
                          context.getString(R.string.deleting_plase_wait))
                      .create();
              progress.setCancelable(false);
              progress.show();
              TaskExecutor.executeAsyncProvideError(
                  () -> {
                    return FileUtil.delete(file.getAbsolutePath());
                  },
                  (result, error) -> {
                    concluded.concluded(file);
                    progress.cancel();
                  });
            })
        .setNegativeButton(R.string.cancel, null)
        .show();
  }

  public static void takeFilePermissions(Activity activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      Intent intent = new Intent();
      intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
      Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
      intent.setData(uri);
      activity.startActivity(intent);
    } else {
      ActivityCompat.requestPermissions(
          activity,
          new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE
          },
          1);
    }
  }

  public static boolean isPermissionGaranted(Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      return Environment.isExternalStorageManager();
    } else {
      return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
          == PackageManager.PERMISSION_GRANTED;
    }
  }

  public interface OnFileRenamed {
    void onFileRenamed(File oldFile, File newFile);
  }

  public interface Concluded {
    void concluded(File file);
  }
}
