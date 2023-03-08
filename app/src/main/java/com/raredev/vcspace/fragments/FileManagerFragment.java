package com.raredev.vcspace.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.raredev.common.util.Utils;
import com.raredev.vcspace.R;
import com.raredev.vcspace.adapters.FilesAdapter;
import com.raredev.vcspace.databinding.FragmentFileManagerBinding;
import com.raredev.vcspace.fragments.callback.FileManagerCallBack;
import com.raredev.vcspace.util.ApkInstaller;
import com.raredev.vcspace.util.FileManagerUtils;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("deprecation")
public class FileManagerFragment extends Fragment {
  private FragmentFileManagerBinding binding;

  private FileManagerCallBack callback;

  private List<File> mFiles = new LinkedList<>();
  private FilesAdapter mAdapter;

  private File currentDir = new File(Environment.getExternalStorageDirectory().toString());

  @Override
  public void onAttach(Context activity) {
    super.onAttach(activity);
    callback = (FileManagerCallBack) activity;
  }

  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentFileManagerBinding.inflate(inflater, container, false);
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
            if (position == 0) {
              if (currentDir.getAbsolutePath().equals("/storage/emulated/0")) return;
              reloadFiles(currentDir.getParentFile());
              return;
            }

            if (mFiles.get(position) != null) {
              if (mFiles.get(position).isDirectory()) {
                reloadFiles(mFiles.get(position));

              } else {
                if (FileManagerUtils.isValidTextFile(mFiles.get(position).getName())) {
                  if (callback != null) {
                    callback.onFileClicked(mFiles.get(position));
                  }
                } else if (mFiles.get(position).getName().endsWith(".apk")) {
                  ApkInstaller.installApplication(getContext(), mFiles.get(position));
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
                  if (title == getActivity().getResources().getString(R.string.menu_rename)) {
                    FileManagerUtils.renameFile(
                        getActivity(),
                        mFiles.get(position),
                        (oldFile, newFile) -> {
                          callback.onFileRenamed(oldFile, newFile);
                          reloadFiles();
                        });
                  } else {
                    FileManagerUtils.deleteFile(
                        getActivity(),
                        mFiles.get(position),
                        () -> {
                          callback.onFileDeleted();
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
        getActivity(),
        getResources().getString(R.string.refresh),
        R.drawable.ic_baseline_restart_alt_24,
        (v) -> {
          reloadFiles(currentDir);
        });

    binding.navigationSpace.addItem(
        getActivity(),
        getResources().getString(R.string.create),
        R.drawable.ic_add,
        (v) -> {
          FileManagerUtils.createFile(getActivity(), currentDir, () -> reloadFiles(currentDir));
        });

    binding.swiperefreshlayout.setOnRefreshListener(
        () -> {
          reloadFiles();
          binding.swiperefreshlayout.setRefreshing(false);
        });

    binding.rvFiles.setLayoutManager(new LinearLayoutManager(getContext()));
    binding.rvFiles.setAdapter(mAdapter);
  }

  @Override
  public void onStart() {
    super.onStart();
    reloadFiles();
  }

  private void reloadFiles() {
    reloadFiles(currentDir);
  }

  private void reloadFiles(File dir) {
    if (Utils.isPermissionGaranted(getContext())) {
      listArchives(dir);
      runAnimation();
      if (mFiles.size() <= 1) {
        binding.emptyLayout.setVisibility(View.VISIBLE);
      } else {
        binding.emptyLayout.setVisibility(View.GONE);
      }
    } else {
      takePermissions();
    }
  }

  public void listArchives(File dir) {
    currentDir = dir;
    mFiles.clear();

    mFiles.add(new File(".."));

    File[] listFiles = dir.listFiles();
    if (listFiles != null) {
      for (File file : listFiles) {
        mFiles.add(file);
      }
      Collections.sort(mFiles, FileManagerUtils.COMPARATOR);
    }
  }

  private void runAnimation() {
    final LayoutAnimationController controller =
        AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation);
    binding.rvFiles.setLayoutAnimation(controller);
    mAdapter.notifyDataSetChanged();
    binding.rvFiles.scheduleLayoutAnimation();
  }

  private void takePermissions() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      Intent intent = new Intent();
      intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
      Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
      intent.setData(uri);
      startActivity(intent);
    } else {
      ActivityCompat.requestPermissions(
          getActivity(),
          new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE
          },
          1);
    }
  }
}
