package com.raredev.vcspace.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.core.content.ContextCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.raredev.common.util.FileUtil;
import com.raredev.vcspace.R;
import com.raredev.vcspace.adapters.model.FileTemplateModel;
import com.raredev.vcspace.tools.TemplatesParser;
import java.io.File;
import java.util.Comparator;

public class FileManagerUtils {

  public static final Comparator<File> COMPARATOR =
      (file1, file2) -> {
        if (file1.isFile() && file2.isDirectory()) {
          return 1;
        } else if (file2.isFile() && file1.isDirectory()) {
          return -1;
        } else {
          return String.CASE_INSENSITIVE_ORDER.compare(file1.getName(), file2.getName());
        }
      };

  public static boolean isPermissionGaranted(Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      return Environment.isExternalStorageManager();
    } else {
      return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
          == PackageManager.PERMISSION_GRANTED;
    }
  }

  public static boolean isValidTextFile(String filename) {
    return !filename.matches(
        ".*\\.(bin|ttf|png|jpe?g|bmp|mp4|mp3|m4a|iso|so|zip|jar|dex|odex|vdex|7z|apk|apks|xapk)$");
  }

  public static void createFile(Activity act, File file, Concluded concluded) {
    LayoutInflater inflater = act.getLayoutInflater();
    View v = inflater.inflate(R.layout.dialog_input, null);
    EditText et_filename = v.findViewById(R.id.et_input);

    new MaterialAlertDialogBuilder(act)
        .setTitle(R.string.new_file_title)
        .setPositiveButton(
            R.string.file,
            (dlg, i) -> {
              File newFile = new File(file, "/" + et_filename.getText().toString());
              if (!newFile.exists()) {
                if (createFileWithTemplate(newFile)) {
                  concluded.concluded();
                }
              }
            })
        .setNegativeButton(
            R.string.folder,
            (dlg, i) -> {
              File newFolder = new File(file, "/" + et_filename.getText().toString());
              if (!newFolder.exists()) {
                if (newFolder.mkdirs()) {
                  concluded.concluded();
                }
              }
            })
        .setNeutralButton(R.string.cancel, null)
        .setView(v)
        .show();
  }

  public static void renameFile(Activity act, File file, OnFileRenamed onFileRenamed) {
    LayoutInflater inflater = act.getLayoutInflater();
    View v = inflater.inflate(R.layout.dialog_input, null);
    EditText et_filename = v.findViewById(R.id.et_input);
    et_filename.setText(file.getName());

    new MaterialAlertDialogBuilder(act)
        .setTitle(R.string.menu_rename)
        .setPositiveButton(
            R.string.menu_rename,
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
        .setView(v)
        .show();
  }

  public static void deleteFile(Activity act, File file, Concluded concluded) {
    MaterialAlertDialogBuilder dlg_delete = new MaterialAlertDialogBuilder(act);
    dlg_delete.setTitle(R.string.delete);

    dlg_delete.setMessage(
        act.getResources().getString(R.string.delete_message).replace("NAME", file.getName()));
    dlg_delete.setPositiveButton(
        R.string.delete,
        (dlg, i) -> {
          if (FileUtil.delete(file.getAbsolutePath())) {
            concluded.concluded();
          }
        });
    dlg_delete.setNegativeButton(R.string.cancel, null);
    dlg_delete.show();
  }

  private static boolean createFileWithTemplate(File file) {
    String fileExtension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
    String fileName = file.getName().replace("." + fileExtension, "");

    String templateContent = "";
    if (!fileExtension.isEmpty()) {
      for (FileTemplateModel template : TemplatesParser.getTemplates()) {
        if (template.getFileExtension().equals(fileExtension)) {
          templateContent = formatTemplateContent(fileName, template.getTemplateContent());
        }
      }
    }

    return FileUtil.writeFile(file.getAbsolutePath(), templateContent);
  }

  private static String formatTemplateContent(String fileName, String templateContent) {
    if (templateContent.contains("<?NAME?>")) {
      return templateContent.replace("<?NAME?>", fileName);
    }
    return templateContent;
  }

  public interface OnFileRenamed {
    void onFileRenamed(File oldFile, File newFile);
  }

  public interface Concluded {
    void concluded();
  }
}
