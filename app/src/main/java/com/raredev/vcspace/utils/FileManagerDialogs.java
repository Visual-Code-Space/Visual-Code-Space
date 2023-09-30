package com.raredev.vcspace.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.raredev.vcspace.callback.PushCallback;
import com.raredev.vcspace.git.CloneRepository;
import com.raredev.vcspace.models.FileModel;
import com.raredev.vcspace.progressdialog.ProgressDialog;
import com.raredev.vcspace.res.R;
import com.raredev.vcspace.res.databinding.LayoutTextinputBinding;
import com.raredev.vcspace.tasks.TaskExecutor;
import com.raredev.vcspace.tasks.file.DeleteFilesTask;
import java.io.File;
import java.util.List;

public class FileManagerDialogs {

  @SuppressWarnings("deprecation")
  public static void createNew(Context context, File file, PushCallback<File> callback) {
    LayoutTextinputBinding binding = LayoutTextinputBinding.inflate(LayoutInflater.from(context));

    AlertDialog dialog =
        new MaterialAlertDialogBuilder(context)
            .setView(binding.getRoot())
            .setTitle(R.string.create)
            .setPositiveButton(R.string.folder, null)
            .setNegativeButton(R.string.file, null)
            .setNeutralButton(R.string.cancel, null)
            .create();

    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

    dialog.setOnShowListener(
        (p1) -> {
          TextInputEditText et_filename = binding.inputEdittext;
          binding.inputLayout.setHint(context.getString(R.string.file_name_hint));
          et_filename.requestFocus();
          Button createFolder = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
          createFolder.setOnClickListener(
              v -> {
                File newFolder = new File(file, "/" + et_filename.getText().toString());
                if (!newFolder.exists()) {
                  if (newFolder.mkdirs()) {
                    callback.onComplete(newFolder);
                  }
                }
                dialog.dismiss();
              });
          Button createFile = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
          createFile.setOnClickListener(
              v -> {
                File newFile = new File(file, "/" + et_filename.getText().toString());
                if (!newFile.exists()) {
                  if (FileIOUtils.writeFileFromString(newFile, "")) {
                    callback.onComplete(newFile);
                  }
                }
                dialog.dismiss();
              });
        });

    dialog.show();
  }

  public static void renameFile(Context context, File file, PushCallback<File[]> callback) {
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
          TextInputEditText et_filename = binding.inputEdittext;
          Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);

          binding.inputLayout.setHint(R.string.rename_hint);

          String oldFileName = file.getName();

          et_filename.setText(oldFileName);
          int lastIndex = oldFileName.lastIndexOf(".");
          if (lastIndex >= 0) {
            et_filename.setSelection(0, lastIndex);
          } else {
            et_filename.setSelection(oldFileName.length(), oldFileName.length());
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
                    callback.onComplete(new File[] { file, newFile });
                  }
                }
                dialog.dismiss();
              });
        });
    dialog.show();
  }

  public static void deleteFile(
      Context context, List<FileModel> files, PushCallback<List<FileModel>> callback) {
    new MaterialAlertDialogBuilder(context)
        .setTitle(files.size() == 1 ? R.string.delete : R.string.delete_multi)
        .setMessage(
            files.size() == 1
                ? context.getString(R.string.delete_message, files.get(0).getName())
                : context.getString(R.string.delete_count_message, files.size()))
        .setPositiveButton(
            R.string.delete,
            (di, witch) -> {
              ProgressDialog builder =
                  ProgressDialog.create(context)
                      .setTitle(R.string.deleting)
                      .setLoadingMessage(R.string.deleting_please_wait);

              AlertDialog dialog = builder.create();
              dialog.setCancelable(false);
              dialog.show();

              TaskExecutor.executeAsyncProvideError(
                  new DeleteFilesTask(
                      files,
                      message ->
                          ThreadUtils.runOnUiThread(() -> builder.setLoadingMessage(message))),
                  (result, error) -> {
                    dialog.cancel();
                    if (result) {
                      ToastUtils.showShort(
                          context.getString(R.string.deleted_message), ToastUtils.TYPE_SUCCESS);
                    }
                    callback.onComplete(files);
                  });
            })
        .setNegativeButton(R.string.no, (di, witch) -> di.dismiss())
        .show();
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

  public static void cloneRepoDialog(Context context, File file, PushCallback<File> callback) {
    CloneRepository cloneRepo = new CloneRepository(context);
    cloneRepo.setDirectory(file);
    cloneRepo.cloneRepository();
    cloneRepo.setListener(
        new CloneRepository.CloneListener() {

          @Override
          public void onCloneSuccess(File output) {
            callback.onComplete(output);
          }

          @Override
          public void onCloneFailed(String message) {
            new MaterialAlertDialogBuilder(context)
                .setTitle("Clone failed")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
          }
        });
  }
}
