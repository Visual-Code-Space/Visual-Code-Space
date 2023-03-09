package com.raredev.vcspace.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.raredev.common.util.FileUtil;
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
  private static final String KEY_RECENT_FOLDER = "recentFolderPath";
  private FragmentFileManagerBinding binding;

  private List<File> mFiles = new ArrayList<>();
  private FilesAdapter mAdapter;

  private File currentDir = null;
  private File rootDir = null;

  private ActivityResultLauncher<Intent> mStartForResult;
  private SharedPreferences prefs;

  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentFileManagerBinding.inflate(inflater, container, false);
    prefs = PreferencesUtils.getFileManagerPrefs();
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mAdapter = new FilesAdapter(mFiles);
    mStartForResult =
        requireActivity()
            .registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                  @Override
                  public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                      Intent intent = result.getData();
                      // Handle the Intent
                      Uri uri = intent.getData();
                      if (uri != null) {
                        try {
                          DocumentFile pickedDir = DocumentFile.fromTreeUri(requireContext(), uri);
                          rootDir = FileUtil.getFileFromUri(requireContext(), pickedDir.getUri());
                          prefs.edit().putString(KEY_RECENT_FOLDER, rootDir.toString()).apply();
                          reloadFiles(rootDir);
                        } catch (Exception e) {
                          e.printStackTrace();
                        }
                      }
                      updateViewsVisibility();
                    }
                  }
                });

    mAdapter.setFileListener(
        new FilesAdapter.FileListener() {
          @Override
          public void onFileClick(int position, View v) {
            if (position == 0) {
              if (currentDir.getAbsolutePath().equals(rootDir.toString())) return;
              reloadFiles(currentDir.getParentFile());
              return;
            }

            if (mFiles.get(position) != null) {
              if (mFiles.get(position).isDirectory()) {
                reloadFiles(mFiles.get(position));

              } else {
                if (FileManagerUtils.isValidTextFile(mFiles.get(position).getName())) {
                  ((MainActivity) requireActivity()).getEditorManager().openFile(mFiles.get(position));
                } else if (mFiles.get(position).getName().endsWith(".apk")) {
                  ApkInstaller.installApplication(requireContext(), mFiles.get(position));
                }
              }
            }
          }

          @Override
          public boolean onFileLongClick(int position, View v) {
            if (position == 0) return false;
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

    binding.navigationSpace.addItem(
        requireActivity(),
        getResources().getString(R.string.refresh),
        R.drawable.ic_refresh,
        (v) -> {
          reloadFiles(currentDir);
        });

    binding.navigationSpace.addItem(
        requireActivity(),
        getResources().getString(R.string.create),
        R.drawable.ic_add,
        (v) -> {
          FileManagerUtils.createFile(requireActivity(), currentDir, () -> reloadFiles());
        });
    binding.rvFiles.setLayoutManager(new LinearLayoutManager(requireContext()));
    binding.rvFiles.setAdapter(mAdapter);

    ViewUtils.rotateChevron(ViewUtils.isExpanded(binding.containerOpen), binding.downButton);
    binding.expandCollapse.setOnClickListener(
        v -> {
          if (ViewUtils.isExpanded(binding.containerOpen)) {
            ViewUtils.collapse(binding.containerOpen);
            ViewUtils.rotateChevron(false, binding.downButton);
          } else {
            ViewUtils.expand(binding.containerOpen);
            ViewUtils.rotateChevron(true, binding.downButton);
          }
        });
    binding.openFolder.setOnClickListener(
        v -> {
          mStartForResult.launch(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE));
        });
    binding.openRecent.setOnClickListener(
        v -> {
          String recentFolderPath = prefs.getString(KEY_RECENT_FOLDER, "");
          if (!recentFolderPath.isEmpty()) {
            rootDir = new File(recentFolderPath);
            reloadFiles(rootDir);
          }
          updateViewsVisibility();
        });
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

  private void reloadFiles() {
    reloadFiles(currentDir);
  }

  private void reloadFiles(File dir) {
    if (FileManagerUtils.isPermissionGaranted(requireContext())) {
      listArchives(dir);

      if (mFiles.size() <= 1) {
        binding.emptyLayout.setVisibility(View.VISIBLE);
      } else {
        binding.emptyLayout.setVisibility(View.GONE);
      }
    } else {
      takeFilePermissions();
    }
  }

  public void listArchives(File dir) {
    currentDir = dir;
    List<File> filesList = new ArrayList<>();
    filesList.add(new File(".."));

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

  private void updateViewsVisibility() {
    if (mFiles.isEmpty()) {
      binding.expandCollapse.setVisibility(View.VISIBLE);
      binding.containerOpen.setVisibility(View.VISIBLE);
      binding.fileManager.setVisibility(View.GONE);
      binding.navigationSpace.setVisibility(View.GONE);
    } else {
      binding.expandCollapse.setVisibility(View.GONE);
      binding.containerOpen.setVisibility(View.GONE);
      binding.fileManager.setVisibility(View.VISIBLE);
      binding.navigationSpace.setVisibility(View.VISIBLE);
    }
  }

  private void takeFilePermissions() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      Intent intent = new Intent();
      intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
      Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
      intent.setData(uri);
      startActivity(intent);
    } else {
      requestPermissions(
          new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE
          },
          1);
    }
  }
}
