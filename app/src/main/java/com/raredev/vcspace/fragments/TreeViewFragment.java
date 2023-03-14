package com.raredev.vcspace.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.raredev.common.util.DialogUtils;
import com.raredev.vcspace.R;
import com.raredev.vcspace.activity.MainActivity;
import com.raredev.vcspace.adapters.ListDialogAdapter;
import com.raredev.vcspace.databinding.FragmentTreeViewBinding;
import com.raredev.vcspace.databinding.LayoutListDialogBinding;
import com.raredev.vcspace.models.DialogListModel;
import com.raredev.vcspace.ui.tree.TreeUtils;
import com.raredev.vcspace.ui.tree.holder.FileViewHolder;
import com.raredev.vcspace.util.ApkInstaller;
import com.raredev.vcspace.util.FileManagerUtils;
import com.raredev.vcspace.util.PreferencesUtils;
import com.raredev.vcspace.util.ViewUtils;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("deprecation")
public class TreeViewFragment extends Fragment
    implements TreeNode.TreeNodeClickListener, TreeNode.TreeNodeLongClickListener {
  public static final String KEY_STORED_TREE_STATE = "treeState";
  private FragmentTreeViewBinding binding;

  private String savedState;
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
    binding = FragmentTreeViewBinding.inflate(inflater, container, false);
    ViewUtils.rotateChevron(ViewUtils.isExpanded(binding.containerOpen), binding.downButton);

    binding.expandCollapse.setOnClickListener(
        v -> {
          expandCollapseView();
        });

    binding.openFolder.setOnClickListener(
        v -> {
          ((ToolsFragment) getParentFragment())
              .mStartForResult.launch(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE));
        });

    binding.openRecent.setOnClickListener(
        v -> {
          tryOpenRecentFolder();
        });

    binding.refresh.setOnClickListener(
        v -> {
          if (treeView != null) savedState = treeView.getSaveState();
          loadTreeView(mRoot.childAt(0).getValue());
        });

    binding.close.setOnClickListener(
        v -> {
          new MaterialAlertDialogBuilder(requireContext())
              .setTitle(R.string.close_folder_title)
              .setMessage(R.string.close_folder_message)
              .setPositiveButton(R.string.close, (di, which) -> doCloseFolder(false))
              .setNegativeButton(R.string.cancel, (di, which) -> di.dismiss())
              .show();
        });
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (savedInstanceState != null) {
      savedState = savedInstanceState.getString(KEY_STORED_TREE_STATE, null);
    }
    if (PreferencesUtils.useOpenRecentsAutomatically()) {
      tryOpenRecentFolder();
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (treeView != null) savedState = treeView.getSaveState();
    outState.putString(KEY_STORED_TREE_STATE, savedState);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
    treeView = null;
  }

  @Override
  public boolean onLongClick(TreeNode node, Object value) {
    BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
    LayoutListDialogBinding bind = LayoutListDialogBinding.inflate(getLayoutInflater());
    dialog.setContentView(bind.getRoot());

    List<DialogListModel> options = new ArrayList<>();
    if (node.getValue().isDirectory()) {
      options.add(
          new DialogListModel(R.drawable.file_plus_outline, getString(R.string.new_file_title)));
      options.add(
          new DialogListModel(
              R.drawable.folder_plus_outline, getString(R.string.new_folder_title)));
    }
    if (!node.getValue().equals(mRoot.childAt(0).getValue())) {
      options.add(new DialogListModel(R.drawable.file_rename, getString(R.string.rename)));
      options.add(new DialogListModel(R.drawable.delete_outline, getString(R.string.delete)));
    }
    ListDialogAdapter adapter = new ListDialogAdapter(options);

    File file = (File) value;
    adapter.setListener(
        (v, position) -> {
          String label = options.get(position).label;
          if (label.equals(getString(R.string.new_file_title))) {
            FileManagerUtils.createFile(
                requireActivity(),
                file,
                (newFile) -> {
                  TreeUtils.addNewChild(requireContext(), node, newFile);
                  requireExpansion(node);
                });
          } else if (label.equals(getString(R.string.new_folder_title))) {
            FileManagerUtils.createFolder(
                requireActivity(),
                file,
                (newFolder) -> {
                  TreeUtils.addNewChild(requireContext(), node, newFolder);
                  requireExpansion(node);
                });
          }
          if (label.equals(getString(R.string.rename))) {
            FileManagerUtils.renameFile(
                requireContext(),
                file,
                (oldFile, newFile) -> {
                  requireExpansion(node.getParent());
                });
          } else if (label.equals(getString(R.string.delete))) {
            FileManagerUtils.deleteFile(
                requireContext(),
                file,
                (deletedFile) -> {
                  ((MainActivity) requireActivity()).getEditorManager().onFileDeleted();
                  treeView.removeNode(node);
                });
          }
          dialog.cancel();
        });

    bind.title.setText(file.getName());
    bind.subtitle.setText(file.getAbsolutePath());
    bind.list.setLayoutManager(new LinearLayoutManager(requireContext()));
    bind.list.setAdapter(adapter);
    dialog.show();
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
      if (FileManagerUtils.isValidTextFile(file.getName()))
        ((MainActivity) requireActivity()).getEditorManager().openFile(file);
    } else {
      if (node.isExpanded()) {
        TreeUtils.collapseNode(treeView, binding.treeView, node);
        return;
      }
      requireExpansion(node);
    }
  }

  public void doCloseFolder(boolean updatePrefs) {
    if (mRoot != null) {
      mRoot.getChildren().clear();
      mRoot = null;
      treeView = null;

      if (updatePrefs)
        PreferencesUtils.getToolsPrefs()
            .edit()
            .putString(PreferencesUtils.KEY_RECENT_FOLDER, "")
            .apply();
      updateViewsVisibility();
    }
  }

  public void loadTreeView(File rootFile) {
    if (getContext() == null) {
      return;
    }
    if (!FileManagerUtils.isPermissionGaranted(requireContext())) {
      FileManagerUtils.takeFilePermissions(requireActivity());
    }
    doCloseFolder(false);
    mRoot = new TreeNode(new File(""));
    mRoot.setViewHolder(new FileViewHolder(requireContext()));
    TreeNode newRoot = TreeNode.root(rootFile);
    newRoot.setViewHolder(new FileViewHolder(requireContext()));
    mRoot.addChild(newRoot);

    TreeUtils.listNode(
        requireContext(),
        newRoot,
        () -> {
          treeView = new AndroidTreeView(requireContext(), mRoot, R.drawable.ripple_effect);
          treeView.setUseAutoToggle(false);
          treeView.setDefaultNodeClickListener(this);
          treeView.setDefaultNodeLongClickListener(this);

          if (treeView != null) {
            var view = treeView.getView();

            binding.horizontalScroll.removeAllViews();
            binding.horizontalScroll.addView(view);
            tryRestoreSavedState();
          }
        });
    updateViewsVisibility();
  }

  private void tryRestoreSavedState() {
    if (savedState != null) {
      String[] openNodes = savedState.split(AndroidTreeView.NODES_PATH_SEPARATOR);
      treeView.collapseAll();
      restoreNodeState(mRoot, new HashSet<>(Arrays.asList(openNodes)));
    }
  }

  private void restoreNodeState(TreeNode node, Set<String> openNodes) {
    for (TreeNode child : node.getChildren()) {
      if (openNodes.contains(child.getPath())) {
        TreeUtils.listNode(
            requireContext(),
            child,
            () -> {
              TreeUtils.expandNode(treeView, binding.treeView, child);
            });
        restoreNodeState(child, openNodes);
      }
    }
  }

  private void requireExpansion(TreeNode node) {
    TreeUtils.setLoading(node, true);
    TreeUtils.listNode(
        requireContext(),
        node,
        () -> {
          TreeUtils.setLoading(node, false);
          TreeUtils.expandNode(treeView, binding.treeView, node);
        });
  }

  private void tryOpenRecentFolder() {
    try {
      String recentFolderPath =
          PreferencesUtils.getToolsPrefs().getString(PreferencesUtils.KEY_RECENT_FOLDER, "");
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
          getString(R.string.error_treeview_opening_recent_files) + "\n\n" + e.toString());
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
      binding.treeView.setVisibility(View.GONE);
      binding.refresh.setVisibility(View.INVISIBLE);
      binding.close.setVisibility(View.INVISIBLE);
    } else {
      binding.folderName.setText(mRoot.childAt(0).getValue().getName());
      binding.containerOpen.setVisibility(View.GONE);
      binding.treeView.setVisibility(View.VISIBLE);
      binding.refresh.setVisibility(View.VISIBLE);
      binding.close.setVisibility(View.VISIBLE);
    }
  }
}
