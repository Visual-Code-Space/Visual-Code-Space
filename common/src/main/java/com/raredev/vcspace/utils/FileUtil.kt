package com.raredev.vcspace.utils;

import android.content.Context;
import com.blankj.utilcode.util.FileUtils;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
      return path.substring(0, index);
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

  public static String readFromAsset(Context ctx, String path) {
    try {
      // Get the input stream from the asset
      InputStream inputStream = ctx.getAssets().open(path);

      // Create a byte array output stream to store the read bytes
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

      // Create a buffer of 1024 bytes
      byte[] _buf = new byte[1024];
      int i;

      // Read the bytes from the input stream, write them to the output stream and close the streams
      while ((i = inputStream.read(_buf)) != -1) {
        outputStream.write(_buf, 0, i);
      }
      outputStream.close();
      inputStream.close();

      // Return the content of the output stream as a String
      return outputStream.toString();
    } catch (Exception e) {
      e.printStackTrace();
    }

    // If an exception occurred, return an empty String
    return "";
  }
}
