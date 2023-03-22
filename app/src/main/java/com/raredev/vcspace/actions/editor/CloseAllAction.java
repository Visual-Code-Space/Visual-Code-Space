package com.raredev.vcspace.actions.editor;

import com.raredev.vcspace.R;
import com.raredev.vcspace.activity.MainActivity;

public class CloseAllAction extends EditorAction {

  @Override
  public void performAction() {
    var main = (MainActivity) getActionEvent().getData("activity");
    main.editorManager.closeAllFiles();

    main.invalidateOptionsMenu();
  }

  @Override
  public int getTitle() {
    return R.string.close_all;
  }
}
