package com.raredev.vcspace;

import android.content.Context;
import android.content.Intent;
import com.raredev.vcspace.activity.WebViewActivity;
import java.io.File;

public class SimpleExecuter {

  public SimpleExecuter(Context context, File file) {
    if (file.getName().endsWith(".html")) {
      execute(context, file);
    }
  }

  public static boolean isExecutable(File file) {
    if (file == null) return false;
    String fileName = file.getName();
    switch (fileName.substring(fileName.lastIndexOf("."), fileName.length()).toLowerCase()) {
      case ".html":
        return true;
      default:
        return false;
    }
  }

  private void execute(Context context, File file) {
    Intent it = new Intent(context, WebViewActivity.class);
    it.putExtra("html_file", file.getAbsolutePath());
    context.startActivity(it);
  }
}
