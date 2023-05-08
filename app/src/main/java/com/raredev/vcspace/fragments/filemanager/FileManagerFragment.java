package com.raredev.vcspace.fragments.filemanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.TooltipCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.blankj.utilcode.util.ClipboardUtils;
import com.raredev.vcspace.R;
import com.raredev.vcspace.activity.MainActivity;
import com.raredev.vcspace.databinding.FragmentFileManagerBinding;
import com.raredev.vcspace.fragments.filemanager.adapters.DirectoryAdapter;
import com.raredev.vcspace.fragments.filemanager.adapters.FileAdapter;
import com.raredev.vcspace.fragments.filemanager.models.FileModel;
import com.raredev.vcspace.fragments.filemanager.viewmodel.FileListViewModel;
import com.raredev.vcspace.fragments.filemanager.git.CloneRepository;
import com.raredev.vcspace.task.TaskExecutor;
import com.raredev.vcspace.util.ApkInstaller;
import com.raredev.vcspace.util.DialogUtils;
import com.raredev.vcspace.util.FileUtil;
import com.raredev.vcspace.util.ILogger;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FileManagerFragment extends Fragment implements FileAdapter.FileListener {
  private static final String LOG = "FileManagerFragment";
  private FragmentFileManagerBinding binding;

  private FileListViewModel viewModel;

  private DirectoryAdapter mDirectoriesAdapter;
  private FileAdapter mFilesAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    viewModel = new ViewModelProvider((ViewModelStoreOwner) this).get(FileListViewModel.class);
  }

  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentFileManagerBinding.inflate(inflater, container, false);

    TooltipCompat.setTooltipText(binding.gitTools, getString(R.string.git));
    TooltipCompat.setTooltipText(binding.topbarMenu, getString(R.string.folder));
    binding.gitTools.setOnClickListener(
        v -> {
          PopupMenu pm = new PopupMenu(requireContext(), v);
          pm.getMenu().add(R.string.clone_repo);
          pm.setOnMenuItemClickListener(
              item -> {
                if (item.getTitle() == getString(R.string.clone_repo)) {
                  CloneRepository cloneRepo = new CloneRepository(requireActivity());
                  cloneRepo.setDirectory(viewModel.getCurrentDir().toFile());
                  cloneRepo.cloneRepository();
                  cloneRepo.setListener(
                      new CloneRepository.CloneListener() {

                        @Override
                        public void onCloneSuccess(File output) {
                          viewModel.setCurrentDir(FileModel.fileToFileModel(output));
                          ILogger.info(LOG, "Cloned to: " + output.toString());
                        }

                        @Override
                        public void onCloneFailed(String message) {
                          DialogUtils.newErrorDialog(requireActivity(), "Clone failed", message);
                          ILogger.error(LOG, "Clone failed: " + message);
                        }
                      });
                }
                return true;
              });
          pm.show();
        });

    binding.topbarMenu.setOnClickListener(
        v -> {
          PopupMenu pm = new PopupMenu(requireContext(), v);
          pm.getMenu().add(R.string.new_file_title);
          pm.getMenu().add(R.string.new_folder_title);
          pm.getMenu().add(R.string.refresh);
          pm.setOnMenuItemClickListener(
              item -> {
                if (item.getTitle() == getString(R.string.new_file_title)) {
                  FileManagerDialogs.createFile(
                      requireContext(),
                      viewModel.getCurrentDir().toFile(),
                      (newFile) -> refreshFiles());
                } else if (item.getTitle() == getString(R.string.new_folder_title)) {
                  FileManagerDialogs.createFolder(
                      requireContext(),
                      viewModel.getCurrentDir().toFile(),
                      (newFolder) -> refreshFiles());
                } else if (item.getTitle() == getString(R.string.refresh)) {
                  refreshFiles();
                }
                return true;
              });
          pm.show();
        });

    setupRecyclerView();

    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel
        .getCurrentDirLiveData()
        .observe(
            getViewLifecycleOwner(),
            (dir) -> {
              listArchives(dir.toFile());
              viewModel.openDirectory(dir);
              mDirectoriesAdapter.notifyDataSetChanged();
              binding.rvDir.scrollToPosition(mDirectoriesAdapter.getItemCount() - 1);
            });

    /*if (savedInstanceState != null) {
      DirectoryModel dir = savedInstanceState.getParcelable("currentDir");
      viewModel.setCurrentDir(dir);
    }*/
  }

  @Override
  public void onSaveInstanceState(Bundle outstate) {
    super.onSaveInstanceState(outstate);
    // outstate.putParcelable("currentDir", viewModel.getCurrentDir());
  }

  @Override
  public void onFileClick(FileModel file, View v) {
    if (!file.isFile()) {
      viewModel.setCurrentDir(file);
    } else {
      if (FileUtil.isValidTextFile(file.getName())) {
        ((MainActivity) requireActivity()).openFile(file);
      } else if (file.getName().endsWith(".apk")) {
        ApkInstaller.installApplication(getContext(), file.toFile());
      }
    }
  }

  @Override
  public void onFileLongClick(FileModel file, View v) {}

  @Override
  public void onFileMenuClick(FileModel file, View v) {
    PopupMenu pm = new PopupMenu(requireActivity(), v);
    pm.getMenu().add(R.string.copy_path);
    pm.getMenu().add(R.string.rename);
    pm.getMenu().add(R.string.delete);
    pm.setOnMenuItemClickListener(
        item -> {
          if (item.getTitle() == getString(R.string.copy_path)) {
            ClipboardUtils.copyText(file.getPath());
          } else if (item.getTitle() == getString(R.string.rename)) {
            FileManagerDialogs.renameFile(
                requireContext(), file.toFile(), (oldFile, newFile) -> refreshFiles());
          } else if (item.getTitle() == getString(R.string.delete)) {
            FileManagerDialogs.deleteFile(
                requireContext(), file.toFile(), (deletedFile) -> refreshFiles());
          }
          return true;
        });
    pm.show();
  }

  @Override
  public void onResume() {
    super.onResume();
    refreshFiles();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

  private void setupRecyclerView() {
    mDirectoriesAdapter = new DirectoryAdapter(viewModel);
    mFilesAdapter = new FileAdapter(viewModel);

    mFilesAdapter.setFileListener(this);

    binding.rvDir.setLayoutManager(
        new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
    binding.rvDir.setAdapter(mDirectoriesAdapter);

    binding.rvFiles.setLayoutManager(new LinearLayoutManager(requireContext()));
    binding.rvFiles.setAdapter(mFilesAdapter);
  }

  public void refreshFiles() {
    listArchives(viewModel.getCurrentDirFile());
  }

  public void listArchives(File path) {
    binding.container.setDisplayedChild(1);
    TaskExecutor.executeAsyncProvideError(
        () -> {
          List<FileModel> mFiles = new ArrayList<>();
          File[] files = path.listFiles();
          if (files != null) {
            Arrays.sort(files, FILE_FIRST_ORDER);
            for (File file : files) {
              mFiles.add(FileModel.fileToFileModel(file));
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
        });
  }

  public FileListViewModel getViewModel() {
    return viewModel;
  }

  private static final Comparator<File> FILE_FIRST_ORDER =
      (file1, file2) -> {
        if (file1.isFile() && file2.isDirectory()) {
          return 1;
        } else if (file2.isFile() && file1.isDirectory()) {
          return -1;
        } else {
          return String.CASE_INSENSITIVE_ORDER.compare(file1.getName(), file2.getName());
        }
      };
}
