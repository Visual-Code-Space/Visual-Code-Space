package com.raredev.vcspace.actions.file;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import com.raredev.vcspace.actions.Action;
import com.raredev.vcspace.actions.ActionData;
import com.unnamed.b.atv.model.TreeNode;
import java.io.File;

public abstract class FileAction extends Action {

  public FileAction() {
    location = Action.Location.FILE_TREE;
  }

  @Override
  public void update(@NonNull ActionData data) {
    visible = false;

    TreeNode node = (TreeNode) data.get(TreeNode.class);

    if (node == null) {
      return;
    }

    visible = isApplicable(node.getValue());
    title = getTitle();
    icon = getIcon();
  }

  @DrawableRes
  public abstract int getIcon();

  @StringRes
  public abstract int getTitle();

  public abstract boolean isApplicable(File file);
}
