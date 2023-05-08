package com.raredev.vcspace.actions.main.filetoolbar;

import android.content.Context;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.vcspace.actions.ActionData;
import com.vcspace.actions.Presentation;

public class SaveAllAction extends MainBaseAction {

  @Override
  public void update(@NonNull ActionData data) {
    super.update(data);
    Presentation presentation = getPresentation();
    presentation.setEnabled(false);

    var main = getActivity(data);
    if (main == null) {
      return;
    }
    if (main.getCurrentEditor() == null) {
      return;
    }

    presentation.setEnabled(true);
  }

  @Override
  public void performAction(@NonNull ActionData data, MenuItem item) {
    var activity = getActivity(data);
    activity.saveAllFiles(true);
  }

  @Override
  public String getActionId() {
    return "save.all.action";
  }

  @Override
  public String getTitle(Context context) {
    return context.getString(R.string.menu_save_all);
  }

  @Override
  public int getIcon() {
    return R.drawable.ic_save;
  }
}
