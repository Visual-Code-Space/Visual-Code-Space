package com.raredev.vcspace.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.raredev.vcspace.R;
import com.raredev.vcspace.activity.MainActivity;
import com.raredev.vcspace.adapters.FilesAdapter;
import com.raredev.vcspace.databinding.FragmentFileManagerBinding;
import com.raredev.vcspace.util.ApkInstaller;
import com.raredev.vcspace.util.FileManagerUtils;
import com.raredev.vcspace.util.PreferencesUtils;
import com.raredev.vcspace.util.ViewUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("deprecation")
public class FileManagerFragment extends Fragment {
  private FragmentFileManagerBinding binding;

  private List<File> mFiles = new ArrayList<>();
  private FilesAdapter mAdapter;

  private File currentDir = null;

  public File getCurrentDir() {
    return currentDir;
  }

  private File rootDir = null;

  public void setRootDir(File dir) {
    rootDir = dir;
    reloadFiles(dir);
  }

  public File getRootDir() {
    return rootDir;
  }

  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentFileManagerBinding.inflate(inflater, container, false);
    ViewUtils.rotateChevron(ViewUtils.isExpanded(binding.containerOpen), binding.downButton);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mAdapter = new FilesAdapter(mFiles);

    mAdapter.setFileListener(
        new FilesAdapter.FileListener() {
          @Override
          public void onFileClick(int position, View v) {
            if (!currentDir.getAbsolutePath().equals(rootDir.getAbsolutePath()) && position == 0) {
              reloadFiles(currentDir.getParentFile());
              return;
            }

            if (mFiles.get(position) != null) {
              if (mFiles.get(position).isDirectory()) {
                reloadFiles(mFiles.get(position));

              } else {
                if (FileManagerUtils.isValidTextFile(mFiles.get(position).getName())) {
                  ((MainActivity) requireActivity())
                      .getEditorManager()
                      .openFile(mFiles.get(position));
                } else if (mFiles.get(position).getName().endsWith(".apk")) {
                  ApkInstaller.installApplication(requireContext(), mFiles.get(position));
                }
              }
            }
          }

          @Override
          public boolean onFileLongClick(int position, View v) {
            if (!currentDir.getAbsolutePath().equals(rootDir.getAbsolutePath()) && position == 0) {
              return false;
            }
            PopupMenu menu = new PopupMenu(requireActivity(), v);
            menu.getMenu().add(R.string.menu_rename);
            menu.getMenu().add(R.string.delete);

            menu.setOnMenuItemClickListener(
                (item) -> {
                  String title = (String) item.getTitle();
                  if (title == requireActivity().getResources().getString(R.string.menu_rename)) {
                    FileManagerUtils.renameFile(
                        requireActivity(),
                        mFiles.get(position),
                        (oldFile, newFile) -> {
                          reloadFiles();
                        });
                  } else {
                    FileManagerUtils.deleteFile(
                        requireActivity(),
                        mFiles.get(position),
                        () -> {
                          ((MainActivity) requireActivity()).getEditorManager().onFileDeleted();
                          reloadFiles();
                        });
                  }
                  return true;
                });
            menu.show();
            return true;
          }
        });

    binding.rvFiles.setLayoutManager(new LinearLayoutManager(requireContext()));
    binding.rvFiles.setAdapter(mAdapter);

    binding.expandCollapse.setOnClickListener(
        v -> {
          expandCollapseView();
        });
    binding.openFolder.setOnClickListener(
        v -> {
          Fragment fragment = getParentFragment();
          if (fragment != null && fragment instanceof ToolsFragment) {
            ((ToolsFragment) fragment)
                .mStartForResult.launch(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE));
          }
        });
    binding.openRecent.setOnClickListener(
        v -> {
          String recentFolderPath =
              PreferencesUtils.getFileManagerPrefs().getString(ToolsFragment.KEY_RECENT_FOLDER, "");
          if (!recentFolderPath.isEmpty()) {
            rootDir = new File(recentFolderPath);
            reloadFiles(rootDir);
          }
        });
    binding.refresh.setOnClickListener(
        v -> {
          reloadFiles(currentDir);
        });
    binding.newFolder.setOnClickListener(
        v -> {
          FileManagerUtils.createFolder(requireActivity(), currentDir, () -> reloadFiles());
        });
    binding.newFile.setOnClickListener(
        v -> {
          FileManagerUtils.createFile(requireActivity(), currentDir, () -> reloadFiles());
        });
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

  public void reloadFiles() {
    reloadFiles(currentDir);
  }

  public void reloadFiles(File dir) {
    if (FileManagerUtils.isPermissionGaranted(requireContext())) {
      listArchives(dir);

      if (mFiles.isEmpty()) {
        binding.emptyLayout.setVisibility(View.VISIBLE);
      } else {
        binding.emptyLayout.setVisibility(View.GONE);
      }
      updateViewsVisibility();
    } else {
      FileManagerUtils.takeFilePermissions(requireActivity());
    }
  }

  private void listArchives(File dir) {
    currentDir = dir;
    List<File> filesList = new ArrayList<>();
    if (!currentDir.getAbsolutePath().equals(rootDir.getAbsolutePath())) {
      filesList.add(new File(".."));
    }

    File[] listFiles = dir.listFiles();
    if (listFiles != null) {
      for (File file : listFiles) {
        filesList.add(file);
      }
      Collections.sort(filesList, FileManagerUtils.COMPARATOR);
    }
    mFiles = filesList;
    mAdapter.refresh(mFiles);
  }

  private void expandCollapseView() {
    if (ViewUtils.isExpanded(binding.expandableLayout)) {
      ViewUtils.collapse(binding.expandableLayout);
      ViewUtils.rotateChevron(false, binding.downButton);
    } else {
      ViewUtils.expand(binding.expandableLayout);
      ViewUtils.rotateChevron(true, binding.downButton);
    }
    if (rootDir == null) {
      binding.refresh.setVisibility(View.INVISIBLE);
      binding.newFile.setVisibility(View.INVISIBLE);
      binding.newFolder.setVisibility(View.INVISIBLE);

    } else {
      binding.refresh.setVisibility(View.VISIBLE);
      binding.newFile.setVisibility(View.VISIBLE);
      binding.newFolder.setVisibility(View.VISIBLE);
    }
  }

  private void updateViewsVisibility() {
    if (rootDir == null) {
      binding.folderName.setText(R.string.no_folder_opened);
      binding.containerOpen.setVisibility(View.VISIBLE);
      binding.fileManager.setVisibility(View.GONE);
      binding.refresh.setVisibility(View.INVISIBLE);
      binding.newFile.setVisibility(View.INVISIBLE);
      binding.newFolder.setVisibility(View.INVISIBLE);
    } else {
      binding.folderName.setText(rootDir.getName());
      binding.containerOpen.setVisibility(View.GONE);
      binding.fileManager.setVisibility(View.VISIBLE);
      binding.refresh.setVisibility(View.VISIBLE);
      binding.newFile.setVisibility(View.VISIBLE);
      binding.newFolder.setVisibility(View.VISIBLE);
    }
  }
}
