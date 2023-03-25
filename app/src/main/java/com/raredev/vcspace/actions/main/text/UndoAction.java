package com.raredev.vcspace.actions.main.text;

import androidx.annotation.NonNull;
import com.raredev.vcspace.actions.ActionData;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.raredev.vcspace.R;
import com.raredev.vcspace.ui.editor.manager.EditorManager;

public class UndoAction extends MainBaseAction {

  @Override
  public void update(@NonNull ActionData data) {
    super.update(data);
    visible = false;
    var editor = (EditorManager) data.get(EditorManager.class);

    if (editor == null) {
      return;
    }
    if (editor.getCurrentEditor() == null) {
      return;
    }
    visible = true;
    enabled = editor.getCurrentEditor().canUndo();
  }

  @Override
  public void performAction(ActionData data) {
    var editor = (EditorManager) data.get(EditorManager.class);
    if (editor.getCurrentEditor() != null) {
      editor.getCurrentEditor().undo();
    }
  }

  @Override
  public int getIcon() {
    return R.drawable.ic_undo;
  }

  @Override
  public int getTitle() {
    return R.string.menu_undo;
  }
}
