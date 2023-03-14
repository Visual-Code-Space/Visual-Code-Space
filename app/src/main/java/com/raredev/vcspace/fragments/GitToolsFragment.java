package com.raredev.vcspace.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.blankj.utilcode.util.ToastUtils;
import com.raredev.common.util.DialogUtils;
import com.raredev.vcspace.databinding.FragmentGitToolsBinding;
import com.raredev.vcspace.git.CloneRepository;
import com.raredev.vcspace.git.utils.GitUtils;
import com.raredev.vcspace.util.ViewUtils;
import java.io.File;
import java.io.IOException;
import org.eclipse.jgit.api.errors.GitAPIException;

public class GitToolsFragment extends Fragment {
  private FragmentGitToolsBinding binding;

  private GitUtils repository;

  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentGitToolsBinding.inflate(inflater, container, false);
    ViewUtils.rotateChevron(ViewUtils.isExpanded(binding.expandableLayout), binding.downButton);
    
    binding.cloneRepo.setOnClickListener(
        v -> {
          CloneRepository clone = new CloneRepository(requireContext());
          File idePath = new File(Environment.getExternalStorageDirectory(), "/VCSpace/");
          if (!idePath.exists()) {
            idePath.mkdirs();
          }
          clone.cloneRepository(idePath);
          clone.setListener(
              new CloneRepository.CloneListener() {

                @Override
                public void onCloneSuccess(File output) {
                  Fragment fragment = getParentFragment();
                  if (fragment != null && fragment instanceof ToolsFragment) {
                    ((ToolsFragment) fragment).parseRootDirToFileManager(output);
                  }
                  openRepository(output);
                }

                @Override
                public void onCloneFailed(String message) {
                  DialogUtils.newErrorDialog(requireContext(), "Clone error", message);
                }
              });
        });

    binding.expandCollapse.setOnClickListener(
        v -> {
          expandCollapseView();
        });
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }

  public void openRepository(File dir) {
    try {
      repository = new GitUtils(dir.getAbsolutePath());

      ToastUtils.showShort(repository.getStatusAsString());
      //binding.repositoryName.setText(repository.getStatusAsString());
    } catch (IOException e) {
      e.printStackTrace();
    } catch (GitAPIException e) {
      e.printStackTrace();
    }
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
    /*if (repository.getRepositoryDir() != null) {
      binding.containerTools.setVisibility(View.GONE);
      binding.containerProject.setVisibility(View.VISIBLE);
    } else {
      binding.containerTools.setVisibility(View.VISIBLE);
      binding.containerProject.setVisibility(View.GONE);
    }*/
  }
}
