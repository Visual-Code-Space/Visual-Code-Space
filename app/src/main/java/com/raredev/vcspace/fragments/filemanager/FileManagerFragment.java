package com.raredev.vcspace.fragments.filemanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.TooltipCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.raredev.vcspace.R;
import com.raredev.vcspace.activity.MainActivity;
import com.raredev.vcspace.databinding.FragmentFileManagerBinding;
import com.raredev.vcspace.fragments.filemanager.adapters.DirectoryAdapter;
import com.raredev.vcspace.fragments.filemanager.adapters.FileAdapter;
import com.raredev.vcspace.fragments.filemanager.models.FileModel;
import com.raredev.vcspace.fragments.filemanager.viewmodel.FileListViewModel;
import com.raredev.vcspace.task.TaskExecutor;
import com.raredev.vcspace.util.ApkInstaller;
import com.raredev.vcspace.util.FileUtil;
import com.vcspace.actions.ActionData;
import com.vcspace.actions.ActionManager;
import com.vcspace.actions.location.DefaultLocations;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FileManagerFragment extends Fragment implements FileAdapter.FileListener {

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
          ActionData data = new ActionData();
          data.put(FileManagerFragment.class, this);
          data.put(File.class, viewModel.getCurrentDirFile());
          ActionManager.getInstance().fillMenu(pm.getMenu(), data, DefaultLocations.GIT);
          pm.show();
        });

    binding.topbarMenu.setOnClickListener(
        v -> {
          PopupMenu pm = new PopupMenu(requireContext(), v);
          ActionData data = new ActionData();
          data.put(FileManagerFragment.class, this);
          data.put(File.class, viewModel.getCurrentDirFile());
          Menu menu = pm.getMenu();
          if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
          }
          ActionManager.getInstance().fillMenu(menu, data, DefaultLocations.FILE_TOPBAR);
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
    //outstate.putParcelable("currentDir", viewModel.getCurrentDir());
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
    PopupMenu pm = new PopupMenu(requireContext(), v);

    ActionData data = new ActionData();
    data.put(FileManagerFragment.class, this);
    data.put(FileAdapter.class, mFilesAdapter);
    data.put(File.class, file.toFile());

    Menu menu = pm.getMenu();
    if (menu instanceof MenuBuilder) {
      ((MenuBuilder) menu).setOptionalIconsVisible(true);
    }

    ActionManager.getInstance().fillMenu(menu, data, DefaultLocations.FILE);

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
