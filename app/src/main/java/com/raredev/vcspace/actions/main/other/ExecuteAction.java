package com.raredev.vcspace.actions.main.other;

import androidx.annotation.NonNull;
import com.raredev.vcspace.R;
import com.raredev.vcspace.SimpleExecuter;
import com.raredev.vcspace.actions.ActionData;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.raredev.vcspace.activity.MainActivity;
import com.raredev.vcspace.ui.editor.manager.EditorManager;

public class ExecuteAction extends MainBaseAction {

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
    visible = SimpleExecuter.isExecutable(editorManager.getCurrentEditor().getFile());
  }

  @Override
  public void performAction(ActionData data) {
    EditorManager editorManager = (EditorManager) data.get(EditorManager.class);
    editorManager.saveAllFiles(false);
    new SimpleExecuter(
        (MainActivity) data.get(MainActivity.class), editorManager.getCurrentEditor().getFile());
  }

  @Override
  public int getTitle() {
    return R.string.menu_execute;
  }

  @Override
  public int getIcon() {
    return R.drawable.ic_play;
  }
}
