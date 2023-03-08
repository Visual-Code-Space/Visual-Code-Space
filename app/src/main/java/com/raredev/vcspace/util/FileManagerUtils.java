package com.raredev.vcspace.util;

import android.app.Activity;
import android.content.Context;
import com.raredev.vcspace.adapters.model.FileTemplateModel;
import com.raredev.vcspace.tools.TemplatesParser;
import java.text.DecimalFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.raredev.common.util.FileUtil;
import com.raredev.common.util.Utils;
import com.raredev.vcspace.R;
import java.io.File;
import java.util.Comparator;
import java.util.List;

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

  public static boolean isValidTextFile(String filename) {
    if (filename.endsWith(".ttf")
        || filename.endsWith(".png")
        || filename.endsWith(".jpg")
        || filename.endsWith(".jpeg")
        || filename.endsWith(".bmp")
        || filename.endsWith(".mp4")
        || filename.endsWith(".mp3")
        || filename.endsWith(".m4a")
        || filename.endsWith(".iso")
        || filename.endsWith(".so")
        || filename.endsWith(".zip")
        || filename.endsWith(".jar")
        || filename.endsWith(".dex")
        || filename.endsWith(".odex")
        || filename.endsWith(".vdex")
        || filename.endsWith(".7z")
        || filename.endsWith(".apk")
        || filename.endsWith(".apks")
        || filename.endsWith(".xapk")) {
      return false;
    }
    return true;
  }

  public static void getFilesIcon(Context context, String path, ShapeableImageView img) {
    File file = new File(path);
    if (file.isDirectory()) {
      img.setImageResource(R.drawable.ic_folder);

    } else if (file.isFile()) {
      if (file.getName().endsWith(".zip")) {
        img.setImageResource(R.drawable.ic_zip);

      } else if (file.getName().endsWith(".ttf")) {
        img.setImageResource(R.drawable.ic_font);

      } else if (file.getName().endsWith(".java")) {
        img.setImageResource(R.drawable.language_java);

      } else if (file.getName().endsWith(".kt")) {
        img.setImageResource(R.drawable.language_kotlin);

      } else if (file.getName().endsWith(".php")) {
        img.setImageResource(R.drawable.language_php);

      } else if (file.getName().endsWith(".xml")) {
        img.setImageResource(R.drawable.language_xml);

      } else if (file.getName().endsWith(".json")) {
        img.setImageResource(R.drawable.language_json);

      } else if (file.getName().endsWith(".cpp")) {
        img.setImageResource(R.drawable.language_cpp);

      } else if (file.getName().endsWith(".c#")) {
        img.setImageResource(R.drawable.language_csharp);

      } else if (file.getName().endsWith(".c")) {
        img.setImageResource(R.drawable.language_c);

      } else if (file.getName().endsWith(".html")) {
        img.setImageResource(R.drawable.language_html5);

      } else if (file.getName().endsWith(".css")) {
        img.setImageResource(R.drawable.language_css3);

      } else if (file.getName().endsWith(".js")) {
        img.setImageResource(R.drawable.language_javascript);

      } else if (file.getName().endsWith(".py")) {
        img.setImageResource(R.drawable.language_python);

      } else if (file.getName().endsWith(".go")) {
        img.setImageResource(R.drawable.language_go);

      } else if (file.getName().endsWith(".lua")) {
        img.setImageResource(R.drawable.language_lua);

      } else if (file.getName().endsWith(".txt")) {
        img.setImageResource(R.drawable.language_txt);

      } else if (file.getName().endsWith(".apk")) {
        img.setImageResource(R.drawable.ic_file);

      } else {
        img.setImageResource(R.drawable.ic_file);
      }
    }
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
    String fileName = file.getName().replace("." +fileExtension, "");

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
