package com.raredev.vcspace.actions.main.text;

import android.view.MenuItem;
import androidx.annotation.NonNull;
import com.raredev.vcspace.R;
import com.vcspace.actions.ActionData;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.raredev.vcspace.activity.MainActivity;

public class RedoAction extends MainBaseAction {

  @Override
  public void update(@NonNull ActionData data) {
    super.update(data);
    visible = false;
    var main = getActivity(data);

    if (main == null) {
      return;
    }
    if (main.getCurrentEditor() == null) {
      return;
    }
    visible = true;
    enabled = main.getCurrentEditor().canRedo();
  }

  @Override
  public void performAction(ActionData data) {
    var main = getActivity(data);
    if (main.getCurrentEditor() != null) {
      main.getCurrentEditor().redo();
    }
  }

  @Override
  public int getShowAsAction() {
    return MenuItem.SHOW_AS_ACTION_ALWAYS;
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
