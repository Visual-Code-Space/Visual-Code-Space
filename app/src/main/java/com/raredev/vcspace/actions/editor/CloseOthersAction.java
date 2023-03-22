package com.raredev.vcspace.actions.editor;

import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.ActionData;
import com.raredev.vcspace.actions.ActionEvent;
import com.raredev.vcspace.activity.MainActivity;

public class CloseOthersAction extends EditorAction {

  @Override
  public void performAction() {
    var main = (MainActivity) getActionEvent().getData("activity");
    main.editorManager.closeOthers();

    main.invalidateOptionsMenu();
  }

  @Override
  public int getTitle() {
    return R.string.close_others;
  }
}
