package com.raredev.vcspace.actions.main.text;

import androidx.annotation.NonNull;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.ActionData;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.raredev.vcspace.ui.editor.manager.EditorManager;

public class RedoAction extends MainBaseAction {

  @Override
  public void update(@NonNull ActionData data) {
    super.update(data);
    visible = false;
    var editorManager = (EditorManager) data.get(EditorManager.class);

    if (editorManager == null) {
      return;
    }
    if (editorManager.getCurrentEditor() == null) {
      return;
    }
    visible = true;
    enabled = editorManager.getCurrentEditor().canRedo();
  }

  @Override
  public void performAction(ActionData data) {
    var editorManager = (EditorManager) data.get(EditorManager.class);
    if (editorManager.getCurrentEditor() != null) {
      editorManager.getCurrentEditor().redo();
    }
  }

  @Override
  public int getIcon() {
    return R.drawable.ic_redo;
  }

  @Override
  public int getTitle() {
    return R.string.menu_redo;
  }
}
