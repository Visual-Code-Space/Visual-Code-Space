package com.raredev.vcspace.actions.file;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import com.raredev.vcspace.actions.Action;
import com.raredev.vcspace.actions.ActionEvent;
import com.raredev.vcspace.actions.ActionPlaces;
import com.raredev.vcspace.actions.Presentation;
import com.unnamed.b.atv.model.TreeNode;
import java.io.File;

public abstract class FileAction extends Action {

  @Override
  public void update(ActionEvent event) {
    super.update(event);
    Presentation presentation = event.getPresentation();
    presentation.setVisible(false);

    if (!event.getPlace().equals(ActionPlaces.FILE_MANAGER)) {
      return;
    }

    TreeNode node = (TreeNode) event.getData("node");

    if (node == null) {
      return;
    }

    presentation.setVisible(isApplicable(node.getValue()));

    presentation.setTitle(getTitle());
    presentation.setIcon(getIcon());
  }

  @DrawableRes
  public abstract int getIcon();

  @StringRes
  public abstract int getTitle();

  public abstract boolean isApplicable(File file);
}
