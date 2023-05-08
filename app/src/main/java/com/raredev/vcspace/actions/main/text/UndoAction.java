package com.raredev.vcspace.actions.main.text;

import android.content.Context;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import com.blankj.utilcode.util.KeyboardUtils;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.vcspace.actions.ActionData;
import com.vcspace.actions.Presentation;

public class UndoAction extends MainBaseAction {

  @Override
  public void update(@NonNull ActionData data) {
    super.update(data);
    Presentation presentation = getPresentation();
    presentation.setVisible(false);

    var main = getActivity(data);
    if (main == null) {
      return;
    }
    if (main.getCurrentEditor() == null) {
      return;
    }

    presentation.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    presentation.setVisible(KeyboardUtils.isSoftInputVisible(getActivity(data)));
    presentation.setEnabled(main.getCurrentEditor().getEditor().canUndo());
  }

  @Override
  public void performAction(ActionData data, MenuItem item) {
    var main = getActivity(data);
    if (main.getCurrentEditor() != null) {
      main.getCurrentEditor().undo();
      main.invalidateOptionsMenu();
    }
  }

  @Override
  public String getActionId() {
    return "undo.action";
  }

  @Override
  public String getTitle(Context context) {
    return context.getString(R.string.menu_undo);
  }

  @Override
  public int getIcon() {
    return R.drawable.ic_undo;
  }
}
