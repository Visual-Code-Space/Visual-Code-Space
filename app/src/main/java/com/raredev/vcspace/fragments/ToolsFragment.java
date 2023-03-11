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
import androidx.viewpager2.widget.ViewPager2;
import com.blankj.utilcode.util.ToastUtils;
import com.raredev.common.util.FileUtil;
import com.raredev.vcspace.R;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.raredev.vcspace.adapters.ToolsPagerAdapter;
import com.raredev.vcspace.databinding.FragmentToolsBinding;
import com.raredev.vcspace.fragments.FileManagerFragment;
import com.raredev.vcspace.util.PreferencesUtils;
import java.io.File;

public class ToolsFragment extends Fragment {
  public static final String KEY_RECENT_FOLDER = "recentFolderPath";

  public ActivityResultLauncher<Intent> mStartForResult;

  private FragmentToolsBinding binding;

  private ToolsPagerAdapter adapter;
  private SharedPreferences toolsPrefs;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    toolsPrefs = PreferencesUtils.getFileManagerPrefs();

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
                          parsePickedDirToFileManager(dir);
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
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    adapter = new ToolsPagerAdapter(getChildFragmentManager(), getLifecycle());
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
                    tab.setIcon(
                        AppCompatResources.getDrawable(requireContext(), R.drawable.folder_open));
                    break;
                }
              }
            })
        .attach();
  }

  private void parsePickedDirToFileManager(File dir) {
    Fragment fragment =
        getChildFragmentManager().findFragmentByTag("f" + binding.pager.getCurrentItem());

    if (fragment != null && fragment instanceof FileManagerFragment) {
      var fileManager = (FileManagerFragment) fragment;
      fileManager.onPickedDir(dir);
    }
  }
}
