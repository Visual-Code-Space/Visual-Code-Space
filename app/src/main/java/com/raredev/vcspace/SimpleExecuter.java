package com.raredev.vcspace;

import android.content.Context;
import android.content.Intent;
import com.blankj.utilcode.util.FileIOUtils;
import com.raredev.vcspace.activity.MarkdownViewActivity;
import com.raredev.vcspace.activity.WebViewActivity;
import java.io.File;

public class SimpleExecuter {

  public SimpleExecuter(Context context, File file) {
    String fileName = file.getName();
    switch (fileName.substring(fileName.lastIndexOf("."), fileName.length()).toLowerCase()) {
      case ".html":
        execute(context, file, null);
        break;
      case ".md":
        Intent it = new Intent(context, MarkdownViewActivity.class);
        it.putExtra(MarkdownViewActivity.EXTRA_MARKDOWN, FileIOUtils.readFile2String(file));
        context.startActivity(it);
        break;
    }
  }

  public static boolean isExecutable(String fileName) {
    if (fileName == null || !fileName.contains(".")) return false;
    switch (fileName.substring(fileName.lastIndexOf("."), fileName.length()).toLowerCase()) {
      case ".html":
      case ".md":
        return true;
      default:
        return false;
    }
  }

  private void execute(Context context, File executableFile, String html) {
    Intent it = new Intent(context, WebViewActivity.class);
    it.putExtra(
        "executable_file", executableFile == null ? null : executableFile.getAbsolutePath());
    it.putExtra("html_content", html);
    context.startActivity(it);
  }
}
