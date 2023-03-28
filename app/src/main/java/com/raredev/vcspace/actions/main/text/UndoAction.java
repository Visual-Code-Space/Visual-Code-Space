package com.raredev.vcspace.actions.main.text;

import androidx.annotation.NonNull;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.ActionData;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.raredev.vcspace.activity.MainActivity;

public class UndoAction extends MainBaseAction {

  @Override
  public void update(@NonNull ActionData data) {
    super.update(data);
    visible = false;
    var main = (MainActivity) data.get(MainActivity.class);

    if (main == null) {
      return;
    }
    if (main.getCurrentEditor() == null) {
      return;
    }
    visible = true;
    enabled = main.getCurrentEditor().canUndo();
  }

  @Override
  public void performAction(ActionData data) {
    var main = (MainActivity) data.get(MainActivity.class);
    if (main.getCurrentEditor() != null) {
      main.getCurrentEditor().undo();
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
