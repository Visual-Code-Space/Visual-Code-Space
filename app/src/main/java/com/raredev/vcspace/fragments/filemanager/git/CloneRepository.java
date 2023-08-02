package com.raredev.vcspace.fragments.filemanager.git;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import com.blankj.utilcode.util.ThreadUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.raredev.vcspace.R;
import com.raredev.vcspace.databinding.LayoutTextinputBinding;
import com.raredev.vcspace.fragments.settings.GitSettingsFragment;
import com.raredev.vcspace.progressdialog.ProgressDialog;
import com.raredev.vcspace.progressdialog.ProgressStyle;
import com.raredev.vcspace.task.TaskExecutor;
import com.raredev.vcspace.util.PreferencesUtils;
import com.raredev.vcspace.util.SharedPreferencesKeys;
import com.raredev.vcspace.util.ToastUtils;
import java.io.File;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class CloneRepository {
  private CloneListener listener;
  private Context context;

  private File directory;

  public CloneRepository(Context context) {
    this.context = context;
  }

  public void setListener(CloneListener listener) {
    this.listener = listener;
  }

  public void setDirectory(File directory) {
    this.directory = directory;
  }

  public void cloneRepository() {
    if (verifyCredentials()) {
      GitSettingsFragment.showCredentialDialog(context);
      return;
    }
    LayoutTextinputBinding bind = LayoutTextinputBinding.inflate(LayoutInflater.from(context));
    AlertDialog dialog =
        new MaterialAlertDialogBuilder(context)
            .setView(bind.getRoot())
            .setTitle(R.string.clone_repo)
            .setPositiveButton(R.string.clone, null)
            .setNegativeButton(android.R.string.cancel, null)
            .create();

    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    dialog.setOnShowListener(
        (p1) -> {
          TextInputEditText repoUrl = bind.etInput;
          Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);

          repoUrl.requestFocus();
          positive.setOnClickListener(
              v -> {
                doClone(repoUrl.getText().toString());
                dialog.dismiss();
              });
        });
    dialog.show();
  }

  private Git git = null;

  private void doClone(String repoURL) {
    if (repoURL.isEmpty()) {
      listener.onCloneFailed(context.getString(R.string.clone_error_empty_url));
      return;
    }

    ProgressDialog progressDialog =
        ProgressDialog.create(context)
            .setTitle("Cloning repository")
            .setPositiveButton(android.R.string.cancel, null)
            .setLoadingMessage("Starting...")
            .setProgressStyle(ProgressStyle.LINEAR);
    // .setShowPercentage(true);
    AlertDialog dialog = progressDialog.create();

    var output = new File(directory, extractRepositoryNameFromURL(repoURL));
    var monitor = new CloneProgressMonitor(progressDialog);

    var prefs = PreferencesUtils.getDefaultPrefs();

    var task =
        TaskExecutor.executeAsyncProvideError(
            () -> {
              String url = repoURL.trim();
              if (!url.endsWith(".git")) {
                url += ".git";
              }
              git =
                  Git.cloneRepository()
                      .setCredentialsProvider(
                          new UsernamePasswordCredentialsProvider(
                              prefs.getString(SharedPreferencesKeys.KEY_CREDENTIAL_USERNAME, ""),
                              prefs.getString(SharedPreferencesKeys.KEY_CREDENTIAL_PASSWORD, "")))
                      .setURI(url)
                      .setDirectory(output)
                      .setProgressMonitor(monitor)
                      .call();
              return git;
            });

    if (dialog.isShowing()) {
      dialog
          .getButton(AlertDialog.BUTTON_POSITIVE)
          .setOnClickListener(
              v -> {
                monitor.cancel();
                if (git != null) {
                  git.close();
                }
                task.cancel(true);
              });
    }

    dialog.setCancelable(false);
    dialog.show();

    task.whenComplete(
        (result, error) -> {
          ThreadUtils.runOnUiThread(
              () -> {
                dialog.cancel();
                if (result != null && error == null) {
                  result.close();

                  ToastUtils.showShort(
                      context.getString(R.string.successfully_cloned), ToastUtils.TYPE_ERROR);
                  listener.onCloneSuccess(output);
                  return;
                }
                listener.onCloneFailed(Log.getStackTraceString(error));
              });
        });
  }

  private String extractRepositoryNameFromURL(String url) {
    String repositoryName = "";
    int lastSlashIndex = url.lastIndexOf("/");

    if (lastSlashIndex >= 0 && lastSlashIndex < url.length() - 1) {
      repositoryName = url.substring(lastSlashIndex + 1);

      if (repositoryName.endsWith(".git")) {
        repositoryName = repositoryName.substring(0, repositoryName.length() - 4);
      }
    }

    return repositoryName;
  }

  private boolean verifyCredentials() {
    var prefs = PreferencesUtils.getDefaultPrefs();
    return prefs.getString(SharedPreferencesKeys.KEY_CREDENTIAL_USERNAME, "").isEmpty()
        || prefs.getString(SharedPreferencesKeys.KEY_CREDENTIAL_USERNAME, "").isEmpty();
  }

  public class CloneProgressMonitor implements ProgressMonitor {
    private ProgressDialog progressDialog;
    public boolean cancelled = false;

    public CloneProgressMonitor(ProgressDialog progressDialog) {
      this.progressDialog = progressDialog;
    }

    public void cancel() {
      cancelled = true;
    }

    @Override
    public void start(int totalTask) {
      // progressDialog.setMax(totalTask);
    }

    @Override
    public void beginTask(String title, int totalWork) {
      ThreadUtils.runOnUiThread(
          () -> {
            progressDialog.setLoadingMessage(title);
            // progressDialog.setProgress((totalWork / progressDialog.getMax()) * 100);
          });
    }

    @Override
    public void update(int completed) {}

    @Override
    public void endTask() {}

    @Override
    public boolean isCancelled() {
      return cancelled;
    }

    @Override
    public void showDuration(boolean arg0) {}
  }
  
  public interface CloneListener {
    void onCloneSuccess(File output);

    void onCloneFailed(String message);
  }
}
