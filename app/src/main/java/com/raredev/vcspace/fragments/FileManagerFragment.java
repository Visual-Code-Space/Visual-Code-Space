package com.raredev.vcspace.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import com.raredev.common.task.TaskExecutor;
import com.raredev.common.util.DialogUtils;
import com.raredev.vcspace.R;
import com.raredev.vcspace.activity.MainActivity;
import com.raredev.vcspace.databinding.FragmentFileManagerBinding;
import com.raredev.vcspace.ui.tree.TreeUtils;
import com.raredev.vcspace.ui.tree.holder.FileViewHolder;
import com.raredev.vcspace.util.ApkInstaller;
import com.raredev.vcspace.util.FileManagerUtils;
import com.raredev.vcspace.util.PreferencesUtils;
import com.raredev.vcspace.util.ViewUtils;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;
import java.io.File;

@SuppressWarnings("deprecation")
public class FileManagerFragment extends Fragment
    implements TreeNode.TreeNodeClickListener, TreeNode.TreeNodeLongClickListener {
  public static final String KEY_STORED_TREE_STATE = "treeState";
  private FragmentFileManagerBinding binding;

  private AndroidTreeView treeView;
  private TreeNode mRoot;

  public File getRootDir() {
    if (mRoot == null) {
      return null;
    }
    return mRoot.getValue();
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
          tryOpenRecentDir();
        });
    binding.refresh.setOnClickListener(
        v -> {
          loadTreeView(mRoot.getValue());
        });
    binding.close.setOnClickListener(
        v -> {
          doCloseFolder();
        });

    if (PreferencesUtils.useOpenRecentsAutomatically()) {
      tryOpenRecentDir();
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
    treeView = null;
  }

  @Override
  public boolean onLongClick(TreeNode node, Object value) {
    PopupMenu menu = new PopupMenu(requireActivity(), node.getViewHolder().getView());
    if (node.getValue().isDirectory()) {
      menu.getMenu().add(R.string.new_file_title);
      menu.getMenu().add(R.string.new_folder_title);
    }
    if (!node.getValue().equals(mRoot.childAt(0).getValue())) {
      menu.getMenu().add(R.string.rename);
      menu.getMenu().add(R.string.delete);
    }

    menu.setOnMenuItemClickListener(
        (item) -> {
          String title = (String) item.getTitle();
          if (title == getString(R.string.new_file_title)) {
            FileManagerUtils.createFile(
                requireActivity(),
                node.getValue(),
                (newFile) -> {
                  TreeUtils.addNewChild(requireContext(), node, newFile);
                  TreeUtils.expandNode(treeView, binding.getRoot(), node);
                });
          } else if (title == getString(R.string.new_folder_title)) {
            FileManagerUtils.createFolder(
                requireActivity(),
                node.getValue(),
                (newFolder) -> {
                  TreeUtils.addNewChild(requireContext(), node, newFolder);
                  TreeUtils.expandNode(treeView, binding.getRoot(), node);
                });
          } else if (title == getString(R.string.rename)) {
            FileManagerUtils.renameFile(
                requireContext(),
                node.getValue(),
                (oldFile, newFile) -> {
                  updateNode(node.getParent());
                });
          } else {
            FileManagerUtils.deleteFile(
                requireContext(),
                node.getValue(),
                (deletedFile) -> {
                  ((MainActivity) requireActivity()).getEditorManager().onFileDeleted();
                  treeView.removeNode(node);
                });
          }
          return true;
        });
    menu.show();
    return true;
  }

  @Override
  public void onClick(TreeNode node, Object value) {
    File file = (File) value;

    if (file.isFile()) {
      if (file.getName().endsWith(".apk")) {
        ApkInstaller.installApplication(requireContext(), file);
        return;
      }
      ((MainActivity) requireActivity()).getEditorManager().openFile(file);
    } else {
      if (node.isExpanded()) {
        TreeUtils.collapseNode(treeView, binding.getRoot(), node);
        return;
      }
      TreeUtils.setLoading(node, true);
      TreeUtils.listNode(
          requireContext(),
          node,
          () -> {
            TreeUtils.setLoading(node, false);
            TreeUtils.expandNode(treeView, binding.getRoot(), node);
          });
    }
  }

  public void doCloseFolder() {
    if (mRoot != null) {
      mRoot.getChildren().clear();
      mRoot = null;
      treeView = null;

      PreferencesUtils.getToolsPrefs()
          .edit()
          .putString(ToolsFragment.KEY_RECENT_FOLDER, "")
          .apply();
      updateViewsVisibility();
    }
  }

  public void loadTreeView(File rootFile) {
    doCloseFolder();
    mRoot = new TreeNode(new File(""));
    mRoot.setViewHolder(new FileViewHolder(requireContext()));
    TreeNode newRoot = TreeNode.root(rootFile);
    newRoot.setViewHolder(new FileViewHolder(requireContext()));
    mRoot.addChild(newRoot);

    TaskExecutor.executeAsync(
        () -> {
          TreeUtils.listFilesForNode(requireContext(), newRoot);
          treeView = new AndroidTreeView(requireContext(), mRoot, R.drawable.ripple_effect);
          treeView.setUseAutoToggle(false);
          treeView.setDefaultNodeClickListener(this);
          treeView.setDefaultNodeLongClickListener(this);
          return null;
        },
        (result) -> {
          if (treeView != null) {
            var view = treeView.getView();

            binding.horizontalScroll.removeAllViews();
            binding.horizontalScroll.addView(view);
            TreeUtils.expandNode(treeView, binding.getRoot(), mRoot.childAt(0));
          }
        });

    updateViewsVisibility();
  }

  private void updateNode(TreeNode node) {
    TreeUtils.listNode(
        requireContext(),
        node,
        () -> {
          TreeUtils.expandNode(treeView, binding.getRoot(), node);
        });
  }

  private void tryOpenRecentDir() {
    try {
      String recentFolderPath =
          PreferencesUtils.getToolsPrefs().getString(ToolsFragment.KEY_RECENT_FOLDER, "");
      if (!recentFolderPath.isEmpty()) {
        File recentFolder = new File(recentFolderPath);
        if (recentFolder.exists() && recentFolder.isDirectory()) {
          loadTreeView(new File(recentFolderPath));
        }
      }
    } catch (Throwable e) {
      DialogUtils.newErrorDialog(
          requireContext(),
          getString(R.string.error),
          getString(R.string.error_opening_recent_files) + "\n\n" + e.toString());
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
    updateViewsVisibility();
  }

  private void updateViewsVisibility() {
    if (mRoot == null) {
      binding.folderName.setText(R.string.no_folder_opened);
      binding.containerOpen.setVisibility(View.VISIBLE);
      binding.fileManager.setVisibility(View.GONE);
      binding.refresh.setVisibility(View.INVISIBLE);
      binding.close.setVisibility(View.INVISIBLE);
    } else {
      binding.folderName.setText(mRoot.childAt(0).getValue().getName());
      binding.containerOpen.setVisibility(View.GONE);
      binding.fileManager.setVisibility(View.VISIBLE);
      binding.refresh.setVisibility(View.VISIBLE);
      binding.close.setVisibility(View.VISIBLE);
    }
  }
}
