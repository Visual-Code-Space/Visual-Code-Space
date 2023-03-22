package com.raredev.vcspace.actions.editor;

import com.raredev.vcspace.R;
import com.raredev.vcspace.activity.MainActivity;

public class CloseFileAction extends EditorAction {

  @Override
  public void performAction() {
    var main = (MainActivity) getActionEvent().getData("activity");
    main.editorManager.closeFile(main.viewModel.getCurrentFileIndex());

    main.invalidateOptionsMenu();
  }

  @Override
  public int getTitle() {
    return R.string.close;
  }
}
