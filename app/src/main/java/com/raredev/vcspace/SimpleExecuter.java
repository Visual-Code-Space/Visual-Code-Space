package com.raredev.vcspace;

import android.content.Context;
import android.content.Intent;
import com.blankj.utilcode.util.FileIOUtils;
import com.raredev.vcspace.activity.MarkdownViewActivity;
import com.raredev.vcspace.activity.WebViewActivity;
import com.raredev.vcspace.compiler.Compile;
import com.raredev.vcspace.ui.window.VCSpaceWindowManager;
import com.raredev.vcspace.ui.window.WebViewWindow;
import com.raredev.vcspace.util.PreferencesUtils;
import java.io.File;

public class SimpleExecuter {

  private static Compile compiler;

  public static void run(Context context, File file) {
    String fileName = file.getName();
    String extension =
        fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
    switch (extension) {
      case "html":
        if (PreferencesUtils.useWindows()) {
          var webView =
              (WebViewWindow)
                  VCSpaceWindowManager.getInstance(context)
                      .getWindow(VCSpaceWindowManager.WEBVIEW_WINDOW);
          webView.loadFile(file.getPath());
          webView.show();
        } else {
          execute(context, file);
        }
        break;
      case "md":
        Intent it = new Intent(context, MarkdownViewActivity.class);
        it.putExtra(MarkdownViewActivity.EXTRA_MARKDOWN, FileIOUtils.readFile2String(file));
        context.startActivity(it);
        break;

      default:
        if (compiler == null) compiler = new Compile();
        compiler.showCompileDialog(context, extension, FileIOUtils.readFile2String(file));
    }
  }

  public static boolean isExecutable(String fileName) {
    if (fileName == null || !fileName.contains(".")) return false;
    switch (fileName.substring(fileName.lastIndexOf("."), fileName.length()).toLowerCase()) {
      case ".java":
      case ".kt":
      case ".c":
      case ".cpp":
      case ".cs":
      case ".py":
      case ".html":
      case ".md":
        return true;
      default:
        return false;
    }
  }

  private static void execute(Context context, File executableFile) {
    Intent it = new Intent(context, WebViewActivity.class);
    it.putExtra(
        "executable_file", executableFile == null ? null : executableFile.getAbsolutePath());
    context.startActivity(it);
  }
}
