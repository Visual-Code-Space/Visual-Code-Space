package com.raredev.vcspace.actions.main;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import com.raredev.vcspace.fragments.FileTreeFragment;
import com.unnamed.b.atv.model.TreeNode;
import com.vcspace.actions.Action;
import com.vcspace.actions.ActionData;
import com.vcspace.actions.location.DefaultLocations;
import java.io.File;

public abstract class FileTreeBaseAction extends Action {

  public FileTreeBaseAction() {
    location = DefaultLocations.FILE_TREE;
  }

  @Override
  public void update(@NonNull ActionData data) {
    visible = false;

    TreeNode node = getNode(data);

    if (node == null) {
      return;
    }

    visible = isApplicable(node.getValue());
    title = getFragment(data).getString(getTitle());
    icon = getIcon();
  }

  @DrawableRes
  public abstract int getIcon();

  @StringRes
  public abstract int getTitle();

  public abstract boolean isApplicable(File file);

  public FileTreeFragment getFragment(ActionData data) {
    return (FileTreeFragment) data.get(FileTreeFragment.class);
  }

  public TreeNode getNode(ActionData data) {
    return (TreeNode) data.get(TreeNode.class);
  }
}
