package com.raredev.vcspace.utils;

import android.content.Context;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtil {

  public static boolean isValidTextFile(String filename) {
    return !filename.matches(
        ".*\\.(bin|ttf|png|jpe?g|bmp|mp4|mp3|m4a|iso|so|zip|rar|jar|dex|odex|vdex|7z|apk|apks|xapk)$");
  }

  public static String getParentPath(String path) {
    int index = path.lastIndexOf("/");
    if (index != -1) {
      String parentPath = path.substring(0, index);
      return parentPath;
    }
    return null;
  }

  public static boolean delete(String path) {
    return delete(new File(path));
  }

  public static boolean delete(File file) {
    if (!file.exists()) return false;

    if (file.isFile()) {
      return file.delete();
    }

    File[] fileArr = file.listFiles();

    if (fileArr != null) {
      for (File subFile : fileArr) {
        if (subFile.isDirectory()) {
          delete(subFile);
        }

        if (subFile.isFile()) {
          subFile.delete();
        }
      }
    }

    return file.delete();
  }

  public static String readAssetFile(Context context, String path) {
    StringBuilder sb = new StringBuilder();

    InputStream inputStream = null;
    BufferedReader reader = null;
    try {
      inputStream = context.getAssets().open(path);
      reader = new BufferedReader(new InputStreamReader(inputStream));
      String line = "";
      while ((line = reader.readLine()) != null) {
        sb.append(line).append("\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (inputStream != null) {
          inputStream.close();
        }
        if (reader != null) {
          reader.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return sb.toString();
  }
}
