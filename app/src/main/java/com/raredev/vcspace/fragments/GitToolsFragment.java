package com.raredev.vcspace.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.raredev.common.util.DialogUtils;
import com.raredev.vcspace.databinding.FragmentGitToolsBinding;
import com.raredev.vcspace.git.CloneRepository;
import com.raredev.vcspace.git.LocalRepository;
import com.raredev.vcspace.util.ViewUtils;
import java.io.File;

public class GitToolsFragment extends Fragment {
  private FragmentGitToolsBinding binding;

  private LocalRepository repository = new LocalRepository();

  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentGitToolsBinding.inflate(inflater, container, false);
    ViewUtils.rotateChevron(ViewUtils.isExpanded(binding.expandableLayout), binding.downButton);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

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
  }

  public void openRepository(File dir) {
    /*if (repository.openRepository(dir)) {
      updateViews();
      binding.repositoryName.setText(repository.getName());
    }*/
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
    if (repository.getRepositoryDir() != null) {
      binding.containerTools.setVisibility(View.GONE);
      binding.containerProject.setVisibility(View.VISIBLE);
    } else {
      binding.containerTools.setVisibility(View.VISIBLE);
      binding.containerProject.setVisibility(View.GONE);
    }
  }
}
