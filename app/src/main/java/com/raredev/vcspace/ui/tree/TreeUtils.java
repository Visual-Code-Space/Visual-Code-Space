package com.raredev.vcspace.ui.tree;

import android.content.Context;
import android.view.ViewGroup;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;
import com.raredev.common.task.TaskExecutor;
import com.raredev.vcspace.ui.tree.holder.FileViewHolder;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

public class TreeUtils {
  public static void addNewChild(Context context, TreeNode parent, File file) {
    TreeNode newNode = new TreeNode(file);
    newNode.setViewHolder(new FileViewHolder(context));
    parent.addChild(newNode);
  }

  public static void listNode(Context context, TreeNode node, Runnable post) {
    node.getChildren().clear();
    node.setExpanded(false);
    TaskExecutor.executeAsync(
        () -> {
          listFilesForNode(context, node);
          var temp = node;

          while (temp.size() == 1) {
            temp = temp.childAt(0);
            if (!temp.getValue().isDirectory()) {
              break;
            }
            listFilesForNode(context, temp);
            temp.setExpanded(true);
          }
          return null;
        },
        (result) -> {
          post.run();
        });
  }

  public static void listFilesForNode(Context context, TreeNode parent) {
    File[] files = parent.getValue().listFiles();
    if (files != null) {
      Arrays.sort(files, new SortFileName());
      Arrays.sort(files, new SortFolder());
      for (File file : files) {
        TreeNode child = new TreeNode(file);
        child.setViewHolder(new FileViewHolder(context));
        parent.addChild(child);
      }
    }
  }

  public static void expandNode(AndroidTreeView treeView, ViewGroup v, TreeNode node) {
    if (treeView == null) {
      return;
    }
    TransitionManager.beginDelayedTransition(v, new ChangeBounds());
    treeView.expandNode(node);
    updateToggle(node);
  }

  public static void collapseNode(AndroidTreeView treeView, ViewGroup v, TreeNode node) {
    if (treeView == null) {
      return;
    }
    TransitionManager.beginDelayedTransition(v, new ChangeBounds());
    treeView.collapseNode(node);
    updateToggle(node);
  }

  public static void setLoading(TreeNode node, boolean loading) {
    if (node.getViewHolder() instanceof FileViewHolder) {
      ((FileViewHolder) node.getViewHolder()).setLoading(loading);
    }
  }

  private static void updateToggle(TreeNode node) {
    if (node.getViewHolder() instanceof FileViewHolder) {
      ((FileViewHolder) node.getViewHolder()).rotateChevron(node.isExpanded());
    }
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
