package com.raredev.vcspace.ui.editor.language.html;

import android.content.Context;
import android.content.Intent;

import com.raredev.vcspace.activity.WebViewActivity;

import java.io.File;

public class ExecuteHtml {
  public ExecuteHtml(Context context, File file) {
    if (file.getName().endsWith(".html")) {
      execute(context, file);
    }
  }

  private void execute(Context context, File file) {
    Intent it = new Intent(context, WebViewActivity.class);
    it.putExtra("html_file", file.getAbsolutePath());
    context.startActivity(it);
  }
}
