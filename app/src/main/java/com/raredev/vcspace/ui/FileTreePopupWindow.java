package com.raredev.vcspace.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import com.blankj.utilcode.util.FileUtils;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.elevation.SurfaceColors;
import com.raredev.vcspace.R;
import com.raredev.vcspace.activity.EditorActivity;
import com.raredev.vcspace.databinding.LayoutTreeviewWindowBinding;
import com.raredev.vcspace.models.DocumentModel;
import com.raredev.vcspace.models.holder.FileTreeViewHolder;
import com.raredev.vcspace.task.TaskExecutor;
import com.raredev.vcspace.util.PreferencesUtils;
import com.raredev.vcspace.util.Utils;
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

  public FileTreePopupWindow(Context context, View v) {
    this.context = context;
    binding = LayoutTreeviewWindowBinding.inflate(LayoutInflater.from(context));
    window = new PopupWindow(context);
    
    window.setWidth(Utils.pxToDp(200));
    window.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

    window.setFocusable(true);
    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    window.setContentView(binding.getRoot());
    window.showAsDropDown(v);

    binding
        .getRoot()
        .setOnTouchListener(
            new View.OnTouchListener() {
              @Override
              public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                  dismiss();
                  return true;
                }
                return false;
              }
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
      if (FileUtils.getFileCharsetSimple(file.getPath())
          .equals(PreferencesUtils.getEncodingForOpening())) {
        ((EditorActivity) context).openFile(DocumentModel.fileToDocument(file));
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

  public void dismiss() {
    window.dismiss();
    treeView = null;
    mRoot = null;
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

          if (treeView != null) {
            var view = treeView.getView();

            binding.horizontalScroll.removeAllViews();
            binding.horizontalScroll.addView(view);

            binding.loading.setVisibility(View.GONE);
          }
        });
  }

  public void addNewChild(TreeNode parent, File file) {
    TreeNode newNode = new TreeNode(file);
    newNode.setViewHolder(new FileTreeViewHolder(context));
    parent.addChild(newNode);
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
      Arrays.sort(files, new SortFileName());
      Arrays.sort(files, new SortFolder());
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
    drawable.setCornerRadius(Utils.pxToDp(9));
    drawable.setColor(SurfaceColors.SURFACE_0.getColor(context));
    drawable.setStroke(2, MaterialColors.getColor(context, com.google.android.material.R.attr.colorOutline, 0));
    binding.getRoot().setBackground(drawable);
  }

  public static class SortFileName implements Comparator<File> {
    @Override
    public int compare(File f1, File f2) {
      return f1.getName().compareTo(f2.getName());
    }
  }

  public static class SortFolder implements Comparator<File> {
    @Override
    public int compare(File f1, File f2) {
      if (f1.isDirectory() == f2.isDirectory()) return 0;
      else if (f1.isDirectory() && !f2.isDirectory()) return -1;
      else return 1;
    }
  }
}
