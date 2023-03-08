package com.raredev.common.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtil {

  public static String getExternalStorageDir() {
    return Environment.getExternalStorageDirectory().getAbsolutePath();
  }

  public static boolean rename(String filePath, String name) {
    File file = new File(filePath);

    if (file.exists()) {
      return file.renameTo(new File(file.getParentFile(), name));
    }
    return false;
  }

  public static boolean makeDir(String path) {
    File file = new File(path);
    if (!file.exists()) {
      return file.mkdirs();
    }
    return false;
  }

  public static boolean writeFile(String path, String content) {
    File file = new File(path);
    if (file == null || content == null) return false;
    BufferedWriter bw = null;
    try {
      bw = new BufferedWriter(new FileWriter(file, false));
      bw.write(content);
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    } finally {
      try {
        if (bw != null) {
          bw.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static boolean delete(String path) {
    File file = new File(path);
    if (!file.exists()) return false;

    if (file.isFile()) {
      return file.delete();
    }
    File[] fileArr = file.listFiles();
    if (fileArr != null) {
      for (File subFile : fileArr) {
        if (subFile.isDirectory()) {
          delete(subFile.getAbsolutePath());
        }

        if (subFile.isFile()) {
          subFile.delete();
        }
      }
    }
    return file.delete();
  }

  public static String readFile(String path) {
    StringBuilder sb = new StringBuilder();
    FileReader fr = null;
    try {
      fr = new FileReader(new File(path));

      char[] buff = new char[1024];
      int length = 0;

      while ((length = fr.read(buff)) > 0) {
        sb.append(new String(buff, 0, length));
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (fr != null) {
        try {
          fr.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    return sb.toString();
  }

  public static String readAssetFile(Context ctx, String path) {
    String str = "";
    try {
      BufferedReader myReader =
          new BufferedReader(new InputStreamReader(ctx.getAssets().open(path)));
      String aDataRow = "";
      while ((aDataRow = myReader.readLine()) != null) {
        str += aDataRow + "\n";
      }
      myReader.close();
    } catch (IOException e) {
    }
    return str;
  }
}
