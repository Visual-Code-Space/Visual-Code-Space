package com.raredev.vcspace.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import com.blankj.utilcode.util.SizeUtils;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.elevation.SurfaceColors;
import com.raredev.vcspace.activities.EditorActivity;
import com.raredev.vcspace.adapters.holder.FileTreeViewHolder;
import com.raredev.vcspace.databinding.LayoutTreeviewWindowBinding;
import com.raredev.vcspace.models.FileModel;
import com.raredev.vcspace.res.R;
import com.raredev.vcspace.task.TaskExecutor;
import com.raredev.vcspace.utils.FileUtil;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class FileTreePopupWindow
    implements TreeNode.TreeNodeClickListener, TreeNode.TreeNodeLongClickListener {

  private LayoutTreeviewWindowBinding binding;

  private PopupWindow window;

  private Context context;

  private AndroidTreeView treeView;
  private TreeNode mRoot;

  private String path;

  public FileTreePopupWindow(Context context) {
    this.context = context;
    binding = LayoutTreeviewWindowBinding.inflate(LayoutInflater.from(context));
    window = new PopupWindow(context);

    window.setWidth(SizeUtils.dp2px(210));
    window.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

    window.setFocusable(true);
    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    window.setElevation(5);

    window.setContentView(binding.getRoot());

    window.setOnDismissListener(
        () -> {
          binding.horizontalScroll.removeAllViews();
          treeView = null;
          mRoot = null;
        });

    applyBackground();
  }

  @Override
  public boolean onLongClick(TreeNode node, Object value) {
    return true;
  }

  @Override
  public void onClick(TreeNode node, Object value) {
    File file = (File) value;

    if (file.isFile()) {
      if (FileUtil.isValidTextFile(file.getName())) {
        ((EditorActivity) context).openFile(FileModel.fileToFileModel(file));
        dismiss();
      }
    } else {
      if (node.isExpanded()) {
        collapseNode(node);
        return;
      }
      setLoading(node, true);
      listNode(
          node,
          () -> {
            setLoading(node, false);
            expandNode(node);
          });
    }
  }

  public void show(View target) {
    window.showAsDropDown(target);
  }

  public void dismiss() {
    window.dismiss();
  }

  public void setPath(String path) {
    this.path = path;
    listFiles();
  }

  private void listFiles() {
    treeView = null;
    mRoot = TreeNode.root(new File(path));
    mRoot.setViewHolder(new FileTreeViewHolder(context));

    binding.loading.setVisibility(View.VISIBLE);
    listNode(
        mRoot,
        () -> {
          treeView = new AndroidTreeView(context, mRoot, R.drawable.ripple_effect);
          treeView.setUseAutoToggle(false);
          treeView.setDefaultNodeClickListener(this);
          treeView.setDefaultNodeLongClickListener(this);

          var view = treeView.getView();
          if (view != null) {

            binding.horizontalScroll.removeAllViews();
            binding.horizontalScroll.addView(view);

            binding.loading.setVisibility(View.GONE);
          }
        });
  }

  public void listNode(TreeNode node, Runnable post) {
    node.getChildren().clear();
    node.setExpanded(false);
    TaskExecutor.executeAsync(
        () -> {
          listFilesForNode(node);
          var temp = node;

          while (temp.size() == 1) {
            temp = temp.childAt(0);
            if (!temp.getValue().isDirectory()) {
              break;
            }
            listFilesForNode(temp);
            temp.setExpanded(true);
          }
          return null;
        },
        (result) -> {
          post.run();
        });
  }

  public void listFilesForNode(TreeNode parent) {
    File[] files = parent.getValue().listFiles();
    if (files != null) {
      Arrays.sort(files, FILE_FIRST_ORDER);
      for (File file : files) {
        TreeNode child = new TreeNode(file);
        child.setViewHolder(new FileTreeViewHolder(context));
        parent.addChild(child);
      }
    }
  }

  public void expandNode(TreeNode node) {
    if (treeView == null) {
      return;
    }
    treeView.expandNode(node);
    updateToggle(node);
  }

  public void collapseNode(TreeNode node) {
    if (treeView == null) {
      return;
    }
    node.getChildren().clear();
    treeView.collapseNode(node);
    updateToggle(node);
  }

  public void setLoading(TreeNode node, boolean loading) {
    if (node.getViewHolder() instanceof FileTreeViewHolder) {
      ((FileTreeViewHolder) node.getViewHolder()).setLoading(loading);
    }
  }

  private void updateToggle(TreeNode node) {
    if (node.getViewHolder() instanceof FileTreeViewHolder) {
      ((FileTreeViewHolder) node.getViewHolder()).updateChevron(node.isExpanded());
    }
  }

  private void applyBackground() {
    GradientDrawable drawable = new GradientDrawable();
    drawable.setShape(GradientDrawable.RECTANGLE);
    drawable.setCornerRadius(10);
    drawable.setColor(SurfaceColors.SURFACE_0.getColor(context));
    drawable.setStroke(
        2, MaterialColors.getColor(context, com.google.android.material.R.attr.colorOutline, 0));
    binding.getRoot().setBackground(drawable);
  }
  
  public static final Comparator<File> FILE_FIRST_ORDER =
      (f1, f2) -> {
        if (f1.isFile() && !f2.isFile()) return 1;
        else if (f2.isFile() && !f1.isFile()) return -1;
        else return String.CASE_INSENSITIVE_ORDER.compare(f1.getName(), f2.getName());
      };

  public static class SortFolder implements Comparator<File> {
    @Override
    public int compare(File f1, File f2) {
      if (f1.isDirectory() == f2.isDirectory()) return 0;
      else if (f1.isDirectory() && !f2.isDirectory()) return -1;
      else return 1;
    }
  }
}
