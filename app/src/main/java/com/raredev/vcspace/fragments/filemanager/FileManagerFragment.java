package com.raredev.vcspace.fragments.filemanager;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.TooltipCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.raredev.vcspace.R;
import com.raredev.vcspace.activity.MainActivity;
import com.raredev.vcspace.adapters.FileListAdapter;
import com.raredev.vcspace.databinding.FragmentFileManagerBinding;
import com.raredev.vcspace.ui.viewmodel.FileListViewModel;
import com.raredev.vcspace.util.ApkInstaller;
import com.raredev.vcspace.util.FileUtil;
import com.vcspace.actions.ActionData;
import com.vcspace.actions.ActionManager;
import com.vcspace.actions.location.DefaultLocations;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FileManagerFragment extends Fragment implements FileListAdapter.FileListener {

  private FileListViewModel viewModel;
  private FileListAdapter mAdapter;

  private File currentFolder = new File(Environment.getExternalStorageDirectory().toString());
  private File backFile = new File("..");

  private FragmentFileManagerBinding binding;

  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentFileManagerBinding.inflate(inflater, container, false);

    viewModel = new ViewModelProvider(requireActivity()).get(FileListViewModel.class);

    TooltipCompat.setTooltipText(binding.gitTools, getString(R.string.git));
    TooltipCompat.setTooltipText(binding.topbarMenu, getString(R.string.folder));
    binding.gitTools.setOnClickListener(
        v -> {
          PopupMenu pm = new PopupMenu(requireContext(), v);
          ActionData data = new ActionData();
          data.put(FileManagerFragment.class, this);
          data.put(File.class, currentFolder);
          ActionManager.getInstance().fillMenu(pm.getMenu(), data, DefaultLocations.GIT);
          pm.show();
        });

    binding.topbarMenu.setOnClickListener(
        v -> {
          PopupMenu pm = new PopupMenu(requireContext(), v);
          ActionData data = new ActionData();
          data.put(FileManagerFragment.class, this);
          data.put(File.class, currentFolder);
          Menu menu = pm.getMenu();
          if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
          }
          ActionManager.getInstance().fillMenu(menu, data, DefaultLocations.FILE_TOPBAR);
          pm.show();
        });

    mAdapter = new FileListAdapter(viewModel);
    mAdapter.setFileListener(this);

    binding.rvFiles.setLayoutManager(new LinearLayoutManager(requireContext()));
    binding.rvFiles.setAdapter(mAdapter);
    return binding.getRoot();
  }

  @Override
  public void onFileClick(File file, View v) {
    if (file == backFile) {
      if (currentFolder.getPath().equals(Environment.getExternalStorageDirectory().getPath())) {
        return;
      }
      listArchives(currentFolder.getParentFile());
      return;
    }
    if (file.isDirectory()) {
      listArchives(file);
    } else {
      if (FileUtil.isValidTextFile(file.getName())) {
        ((MainActivity) requireActivity()).openFile(file);
      } else if (file.getName().endsWith(".apk")) {
        ApkInstaller.installApplication(getContext(), file);
      }
    }
  }

  @Override
  public void onFileLongClick(File file, View v) {
    if (file == backFile) {
      return;
    }
  }

  @Override
  public void onFileMenuClick(File file, View v) {
    PopupMenu pm = new PopupMenu(requireContext(), v);

    ActionData data = new ActionData();
    data.put(FileManagerFragment.class, this);
    data.put(FileListAdapter.class, mAdapter);
    data.put(Context.class, requireContext());
    data.put(File.class, file);

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

  public void refreshFiles() {
    listArchives(currentFolder);
  }

  public void listArchives(File path) {
    currentFolder = path;
    viewModel.getFiles().clear();

    List<File> mFiles = viewModel.getFiles();

    mFiles.add(backFile);
    File[] files = path.listFiles();
    if (files != null) {
      Arrays.sort(files, FILE_FIRST_ORDER);
      for (File file : files) {
        mFiles.add(file);
      }
    }
    viewModel.setFiles(mFiles);
    mAdapter.refreshFiles();
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
