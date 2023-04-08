package com.raredev.vcspace.actions.main.filetree;

import androidx.annotation.NonNull;
import com.blankj.utilcode.util.ClipboardUtils;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.main.FileTreeBaseAction;
import com.unnamed.b.atv.model.TreeNode;
import com.vcspace.actions.ActionData;
import java.io.File;

public class CopyPathAction extends FileTreeBaseAction {

  @Override
  public boolean isApplicable(File file) {
    return true;
  }

  @Override
  public void performAction(@NonNull ActionData data) {
    TreeNode node = getNode(data);
    String path = node.getValue().getAbsolutePath();
    
    ClipboardUtils.copyText(path);
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
