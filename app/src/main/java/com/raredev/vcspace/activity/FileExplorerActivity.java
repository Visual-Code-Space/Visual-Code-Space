package com.raredev.vcspace.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.raredev.vcspace.databinding.ActivityChooseFolderBinding;
import com.raredev.vcspace.fragments.filemanager.adapters.DirectoryAdapter;
import com.raredev.vcspace.fragments.filemanager.adapters.FileAdapter;
import com.raredev.vcspace.fragments.filemanager.listeners.FileListResultListener;
import com.raredev.vcspace.fragments.filemanager.models.FileModel;
import com.raredev.vcspace.fragments.filemanager.viewmodel.FileListViewModel;
import com.raredev.vcspace.task.TaskExecutor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FileExplorerActivity extends BaseActivity implements FileAdapter.FileListener {

  public static final int RESULT_CODE = 0;

  private ActivityChooseFolderBinding binding;

  private FileListViewModel viewModel;

  private DirectoryAdapter mDirectoriesAdapter;
  private FileAdapter mFilesAdapter;

  public static void startPickPathActivity(Activity act) {
    var it = new Intent(act, FileExplorerActivity.class);
    act.startActivityForResult(it, RESULT_CODE);
  }

  @Override
  public View getLayout() {
    binding = ActivityChooseFolderBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setSupportActionBar(binding.toolbar);
    binding.toolbar.setNavigationOnClickListener((v) -> onBackPressed());
    viewModel = new ViewModelProvider(this).get(FileListViewModel.class);
    setupRecyclerView();
    viewModel
        .getCurrentDirLiveData()
        .observe(
            this,
            (dir) -> {
              listArchives(dir);
              viewModel.openDirectory(dir);
              mDirectoriesAdapter.notifyDataSetChanged();
              binding.rvDir.scrollToPosition(mDirectoriesAdapter.getItemCount() - 1);
            });

    binding.cancel.setOnClickListener(
        v -> {
          finish();
        });

    binding.select.setOnClickListener(
        v -> {
          Intent data = new Intent();
          data.putExtra("selectedFolder", viewModel.getCurrentDir().getPath());
          setResult(RESULT_OK, data);
          finish();
        });
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }

  @Override
  protected void onResume() {
    super.onResume();
    refreshFiles();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    binding = null;
  }

  @Override
  public void onFileClick(FileModel file, View v) {
    if (!file.isFile()) {
      viewModel.setCurrentDir(file);
    }
  }

  @Override
  public void onFileLongClick(FileModel file, View v) {}

  private void setupRecyclerView() {
    mDirectoriesAdapter = new DirectoryAdapter(viewModel);
    mFilesAdapter = new FileAdapter(viewModel);

    mFilesAdapter.setFileListener(this);

    binding.rvDir.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    binding.rvDir.setAdapter(mDirectoriesAdapter);

    binding.rvFiles.setLayoutManager(new LinearLayoutManager(this));
    binding.rvFiles.setAdapter(mFilesAdapter);
  }

  @Override
  public void onFileMenuClick(FileModel file, View v) {}

  public void refreshFiles() {
    listArchives(viewModel.getCurrentDir());
  }

  public void listArchives(FileModel path) {
    binding.container.setDisplayedChild(1);
    TaskExecutor.executeAsyncProvideError(
        () -> {
          List<FileModel> mFiles = new ArrayList<>();
          path.listFiles(
              new FileListResultListener() {

                @Override
                public void onResult(FileModel[] result) {
                  if (result.length == 0) {
                    return;
                  }
                  Arrays.sort(result, FILE_FIRST_ORDER);
                  for (FileModel file : result) {
                    mFiles.add(file);
                  }
                }
              });
          return mFiles;
        },
        (result, error) -> {
          binding.container.setDisplayedChild(0);
          if (result == null || error != null) {
            return;
          }
          viewModel.setFiles(result);
          mFilesAdapter.refreshFiles();
        });
   /* TaskExecutor.executeAsyncProvideError(
        () -> {
          List<FileModel> mFiles = new ArrayList<>();
          File[] files = path.listFiles();
          if (files != null) {
            Arrays.sort(files, FILE_FIRST_ORDER);
            for (File file : files) {
              if (!file.isFile()) {
                mFiles.add(FileModel.fileToFileModel(file));
              }
            }
          }
          return mFiles;
        },
        (result, error) -> {
          binding.container.setDisplayedChild(0);
          if (result == null || error != null) {
            return;
          }
          viewModel.setFiles(result);
          mFilesAdapter.refreshFiles();
        });*/
  }

  public FileListViewModel getViewModel() {
    return viewModel;
  }

  private static final Comparator<FileModel> FILE_FIRST_ORDER =
      (file1, file2) -> {
        if (file1.isFile() && !file2.isFile()) {
          return 1;
        } else if (file2.isFile() && !file1.isFile()) {
          return -1;
        } else {
          return String.CASE_INSENSITIVE_ORDER.compare(file1.getName(), file2.getName());
        }
      };
}
