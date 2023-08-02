package com.raredev.vcspace.activity;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import com.blankj.utilcode.util.ThreadUtils;
import com.raredev.vcspace.R;
import com.raredev.vcspace.databinding.ActivityLogViewBinding;
import com.raredev.vcspace.progressdialog.ProgressDialog;
import com.raredev.vcspace.task.TaskExecutor;
import com.raredev.vcspace.util.DialogUtils;
import com.raredev.vcspace.util.ILogger;
import io.github.rosemoe.sora.langs.textmate.VCSpaceTMLanguage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class LogViewActivity extends BaseActivity implements ILogger.Observer {
  private static final String LOG_TAG = LogViewActivity.class.getSimpleName();
  private ActivityLogViewBinding binding;

  @Override
  public View getLayout() {
    binding = ActivityLogViewBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setSupportActionBar(binding.toolbar);
    binding.toolbar.setNavigationOnClickListener((v) -> onBackPressed());
    binding.editor.setEditorLanguage(VCSpaceTMLanguage.create("text.log"));
    binding.editor.getProps().autoIndent = false;
    binding.editor.setEditable(false);
    binding.editor.configureEditor();

    binding.fab.setOnClickListener(
        v -> {
          binding.editor.setText("");
          ILogger.clear();
        });
    ILogger.addObserver(this);
  }

  @Override
  protected void onDestroy() {
    binding.editor.release();
    ILogger.addObserver(null);
    super.onDestroy();
    binding = null;
  }

  @Override
  public void onLogUpdated(File logFile) {
    ProgressDialog progress =
        DialogUtils.newProgressDialog(
            this, getString(R.string.loading), getString(R.string.loading_log_file));
    progress.setCancelable(false);
    AlertDialog dialog = progress.create();
    dialog.show();
    TaskExecutor.executeAsyncProvideError(
        () -> {
          updateLogs(
              logFile, (line) -> ThreadUtils.runOnUiThread(() -> binding.editor.appendText(line)));
          /*try {
            BufferedReader reader = new BufferedReader(new FileReader(logFile));
            String line;
            while ((line = reader.readLine()) != null) {

            }
            reader.close();
          } catch (IOException e) {
            e.printStackTrace();
          }*/
          return null;
        },
        (result, error) -> {
          dialog.cancel();
          if (error != null) ILogger.error(LOG_TAG, error.toString());
        });
  }

  private void updateLogs(File logFile, Callback listener) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(logFile));
      String line;
      while ((line = reader.readLine()) != null) {
        listener.update(line + "\n");
      }
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public interface Callback {
    void update(String line);
  }
}
