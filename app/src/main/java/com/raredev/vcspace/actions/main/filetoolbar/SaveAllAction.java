package com.raredev.vcspace.actions.main.filetoolbar;

import androidx.annotation.NonNull;
import com.raredev.vcspace.R;
import com.vcspace.actions.ActionData;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.raredev.vcspace.activity.MainActivity;

public class SaveAllAction extends MainBaseAction {

  @Override
  public void update(@NonNull ActionData data) {
    super.update(data);
    enabled = false;
    var main = getActivity(data);

    if (main == null) {
      return;
    }
    if (main.getCurrentEditor() == null) {
      return;
    }
    enabled = main.getCurrentEditor() != null;
  }

  @Override
  public void performAction(@NonNull ActionData data) {
    getActivity(data).saveAllFiles(true);
  }

  @Override
  public int getTitle() {
    return R.string.menu_save_all;
  }

  @Override
  public int getIcon() {
    return R.drawable.ic_save;
  }
}
