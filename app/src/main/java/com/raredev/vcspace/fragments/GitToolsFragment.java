package com.raredev.vcspace.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.blankj.utilcode.util.ThreadUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.raredev.common.task.TaskExecutor;
import com.raredev.common.util.DialogUtils;
import com.raredev.vcspace.R;
import com.raredev.vcspace.databinding.FragmentGitToolsBinding;
import com.raredev.vcspace.events.FileEvent;
import com.raredev.vcspace.git.CloneRepository;
import com.raredev.vcspace.git.utils.GitUtils;
import com.raredev.common.util.ILogger;
import com.raredev.vcspace.util.ViewUtils;
import java.io.File;
import java.io.IOException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class GitToolsFragment extends Fragment {
  private final String LOG_TAG = GitToolsFragment.class.getSimpleName();
  private FragmentGitToolsBinding binding;

  private CloneRepository cloneRepo;
  private GitUtils repository;
  private File repoPath;

  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentGitToolsBinding.inflate(inflater, container, false);
    cloneRepo = new CloneRepository(requireContext());
    ViewUtils.rotateChevron(ViewUtils.isExpanded(binding.expandableLayout), binding.downButton);

    File idePath = new File(Environment.getExternalStorageDirectory(), "/VCSpace/");
    if (!idePath.exists()) {
      idePath.mkdirs();
    }
    cloneRepo.setDirectory(idePath);
    binding.cloneRepo.setOnClickListener(
        v -> {
          cloneRepo.cloneRepository();
          cloneRepo.setListener(
              new CloneRepository.CloneListener() {

                @Override
                public void onCloneSuccess(File output) {
                  ((ToolsFragment) getParentFragment()).parseRootFolderToFileManager(output);
                  ILogger.info(LOG_TAG, "Cloned to: " + output.toString());
                }

                @Override
                public void onCloneFailed(String message) {
                  DialogUtils.newErrorDialog(requireContext(), "Clone error", message);
                  ILogger.error(LOG_TAG, "Clone failed: " + message);
                }
              });
        });

    binding.expandCollapse.setOnClickListener(
        v -> {
          expandCollapseView();
        });
    binding.initRepo.setOnClickListener(
        v -> {
          if (repoPath != null) {
            AlertDialog progress =
                DialogUtils.newProgressDialog(
                        requireContext(),
                        getString(R.string.initializing),
                        getString(R.string.initializing_message))
                    .create();
            progress.setCancelable(false);
            progress.show();
            TaskExecutor.executeAsyncProvideError(
                () -> {
                  repository = new GitUtils(repoPath);
                  repository.init();
                  // repository.renameBranchToMain();
                  repository.add(".");
                  ThreadUtils.runOnUiThread(
                      () -> {
                        loadRepositoryInformationsTask();
                        ((ToolsFragment) getParentFragment()).treeViewFragment.refresh();
                      });
                  return null;
                },
                (result, error) -> {
                  progress.cancel();
                  if (error != null) ILogger.error(LOG_TAG, error.toString());
                });
          }
        });
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }

  @Override
  public void onResume() {
    super.onResume();
    loadRepositoryInformationsTask();
  }

  @Override
  public void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onStop() {
    EventBus.getDefault().unregister(this);
    super.onStop();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void openRepository(FileEvent event) {
    if (event.getFile() != null) {
      repoPath = new File(event.getFile(), ".git");
      if (repoPath.exists()) doOpenRepository();
    } else {
      repoPath = null;
      repository = null;
    }
    updateViews();
  }

  private void doOpenRepository() {
    try {
      repository = new GitUtils(repoPath);
      ILogger.info(LOG_TAG, "Opened repository");
    } catch (IOException ioe) {
      ILogger.error(LOG_TAG, Log.getStackTraceString(ioe));
    }
  }

  public void loadRepositoryInformationsTask() {
    if (repoPath == null) {
      return;
    }
    if (!repoPath.exists()) {
      binding.modifications.setText(R.string.error_this_folder_is_not_a_repository);
      binding.initRepo.setVisibility(View.VISIBLE);
      ILogger.warning(LOG_TAG, "Current folder is not a git repository.");
      return;
    }
    binding.initRepo.setVisibility(View.GONE);
    binding.modifications.setText(R.string.loading);
    updateProgress(binding.modifications.getText().toString());

    TaskExecutor.executeAsyncProvideError(
        () -> {
          try {
            String info = repository.getStatusLikeTerminal();
            ThreadUtils.runOnUiThread(
                () -> {
                  binding.modifications.setText(info);
                  updateProgress(binding.modifications.getText().toString());
                });
          } catch (GitAPIException | IOException e) {
            ILogger.error(LOG_TAG, Log.getStackTraceString(e));
          }
          return null;
        },
        (result, error) -> {
          if (error != null) {
            binding.modifications.setText(error.toString());
            updateProgress(binding.modifications.getText().toString());
          }
        });
  }

  // I'll make this more beautiful in the future, for now it's just a test
  private String loadRepositoryInformations() throws GitAPIException, IOException {
    StringBuilder sb = new StringBuilder();

    sb.append("------- Current commit -------\n");
    sb.append("Message: " + repository.getCommitMessage(repository.getCurrentCommitId()));
    sb.append("Id: " + repository.getCurrentCommitId().getName() + "\n");

    sb.append("\n------- Modified -------\n");
    for (String modifiedFile : repository.getStatus().getModified()) {
      sb.append(modifiedFile + "\n");
    }

    sb.append("\n------- Untracked -------\n");
    for (String untracked : repository.getStatus().getUntracked()) {
      sb.append(untracked + "\n");
    }
    return sb.toString();
  }

  private void expandCollapseView() {
    if (ViewUtils.isExpanded(binding.expandableLayout)) {
      ViewUtils.collapse(binding.expandableLayout);
      ViewUtils.rotateChevron(false, binding.downButton);
    } else {
      ViewUtils.expand(binding.expandableLayout);
      ViewUtils.rotateChevron(true, binding.downButton);
    }
    updateViews();
  }

  private void updateViews() {
    if (repoPath != null) {
      binding.repositoryName.setText(repoPath.getParentFile().getName());
      binding.containerTools.setVisibility(View.GONE);
      binding.containerRepository.setVisibility(View.VISIBLE);
    } else {
      binding.repositoryName.setText(com.raredev.vcspace.git.R.string.git_tools);
      binding.containerTools.setVisibility(View.VISIBLE);
      binding.containerRepository.setVisibility(View.GONE);
    }
  }

  private void updateProgress(String info) {
    binding.progressIndicator.setVisibility(
        info.equals(getString(R.string.loading)) ? View.VISIBLE : View.GONE);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (repository != null) repository.close();
  }
}
