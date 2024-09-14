package com.teixeira.vcspace;

import android.content.Intent;

import com.teixeira.vcspace.activities.editor.EditorActivity;
import com.teixeira.vcspace.app.VCSpaceApplication;

import java.io.File;

public class main {
  void main() {
    EditorActivity activity = VCSpaceApplication.getInstance().getEditorActivity();

    if (activity != null) {
      File selectedFile = activity.getSelectedEditor().getFile();

      Intent intent = new Intent("com.termux.RUN_COMMAND");
      intent.setPackage("com.termux");
      intent.putExtra("com.termux.RUN_COMMAND_PATH", "/data/data/com.termux/files/usr/bin/clang");
      intent.putExtra("com.termux.RUN_COMMAND_ARGUMENT", new String[]{
        selectedFile.getAbsolutePath(),
        "-shared",
        "-o",
        selectedFile.getParent() + "/" + selectedFile.getName().substring(0, selectedFile.getName().lastIndexOf(".")) + ".so"
      });
      activity.startActivity(intent);
    }
  }
}
