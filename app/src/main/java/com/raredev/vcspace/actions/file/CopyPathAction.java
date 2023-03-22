package com.raredev.vcspace.actions.file;

import com.blankj.utilcode.util.ClipboardUtils;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.ActionEvent;
import com.unnamed.b.atv.model.TreeNode;
import java.io.File;

public class CopyPathAction extends FileAction {

  @Override
  public boolean isApplicable(File file) {
    return true;
  }

  @Override
  public void performAction() {
    TreeNode node = (TreeNode) getActionEvent().getData("node");

    ClipboardUtils.copyText(node.getValue().getAbsolutePath());
  }

  @Override
  public int getIcon() {
    return R.drawable.content_copy;
  }

  @Override
  public int getTitle() {
    return R.string.copy_path;
  }
}
