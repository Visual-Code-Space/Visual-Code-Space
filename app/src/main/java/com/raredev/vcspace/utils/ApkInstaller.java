package com.raredev.vcspace.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.FileProvider;
import com.raredev.vcspace.BuildConfig;
import java.io.File;

/**
 * Adopted from: <a
 * href="https://github.com/tyron12233/CodeAssist/blob/main/app/src/main/java/com/tyron/code/util/ApkInstaller.java">ApkInstaller.java</a>
 */
public class ApkInstaller {

  public static void installApplication(Context context, File file) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setDataAndType(uriFromFile(context, file), "application/vnd.android.package-archive");
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    try {
      context.startActivity(intent);
    } catch (ActivityNotFoundException e) {
      e.printStackTrace();
    }
  }

  public static Uri uriFromFile(Context context, File file) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
    } else {
      return Uri.fromFile(file);
    }
  }
}
