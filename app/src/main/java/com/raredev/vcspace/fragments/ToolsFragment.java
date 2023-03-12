package com.raredev.vcspace.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.documentfile.provider.DocumentFile;
import com.raredev.common.util.FileUtil;
import com.raredev.vcspace.R;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.raredev.vcspace.adapters.ToolsPagerAdapter;
import com.raredev.vcspace.databinding.FragmentToolsBinding;
import com.raredev.vcspace.util.PreferencesUtils;
import java.io.File;

public class ToolsFragment extends Fragment {
  public static final String KEY_RECENT_FOLDER = "recentFolderPath";

  public ActivityResultLauncher<Intent> mStartForResult;

  private FragmentToolsBinding binding;

  private ToolsPagerAdapter adapter;
  private SharedPreferences toolsPrefs;

  private FileManagerFragment fileManagerFragment;
  private GitToolsFragment gitToolsFragment;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    toolsPrefs = PreferencesUtils.getToolsPrefs();

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
                          File dir = FileUtil.getFileFromUri(requireContext(), pickedDir.getUri());
                          parseRootDirToFileManager(dir);
                          toolsPrefs.edit().putString(KEY_RECENT_FOLDER, dir.toString()).apply();
                        } catch (Exception e) {
                          e.printStackTrace();
                        }
                      }
                    }
                  }
                });
  }

  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentToolsBinding.inflate(inflater, container, false);

    fileManagerFragment = new FileManagerFragment();
    gitToolsFragment = new GitToolsFragment();
    adapter = new ToolsPagerAdapter(getChildFragmentManager(), getLifecycle());
    adapter.addFragment(fileManagerFragment);
    adapter.addFragment(gitToolsFragment);

    binding.pager.setUserInputEnabled(false);
    binding.pager.setAdapter(adapter);

    new TabLayoutMediator(
            binding.tabLayout,
            binding.pager,
            new TabLayoutMediator.TabConfigurationStrategy() {
              @Override
              public void onConfigureTab(TabLayout.Tab tab, int position) {
                switch (position) {
                  case 0:
                    tab.setIcon(
                        AppCompatResources.getDrawable(requireContext(), R.drawable.folder_open));
                    break;
                  case 1:
                    tab.setIcon(AppCompatResources.getDrawable(requireContext(), R.drawable.git));
                    break;
                }
              }
            })
        .attach();

    binding.tabLayout.addOnTabSelectedListener(
        new TabLayout.OnTabSelectedListener() {
          @Override
          public void onTabUnselected(TabLayout.Tab tab) {}

          @Override
          public void onTabReselected(TabLayout.Tab tab) {}

          @Override
          public void onTabSelected(TabLayout.Tab tab) {
            if (tab.getPosition() == 1) {
              tryOpenRepository();
            }
          }
        });
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }

  public void reloadFileManagerFiles(File dir) {
    if (fileManagerFragment != null) {
      setCurrentFragment(0);
      fileManagerFragment.reloadFiles(dir);
    }
  }

  public void parseRootDirToFileManager(File dir) {
    if (fileManagerFragment != null && dir != null) {
      setCurrentFragment(0);
      fileManagerFragment.setRootDir(dir);
    }
  }

  public void tryOpenRepository() {
    if (gitToolsFragment != null && fileManagerFragment != null) {
      gitToolsFragment.openRepository(fileManagerFragment.getRootDir());
    }
  }

  private void setCurrentFragment(int index) {
    final var tab = binding.tabLayout.getTabAt(index);
    if (tab != null && !tab.isSelected()) {
      tab.select();
    }
  }
}
