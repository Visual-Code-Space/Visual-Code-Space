package com.raredev.vcspace.actions.main.filetab;

import androidx.annotation.NonNull;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.main.FileTabBaseAction;
import com.raredev.vcspace.activity.MainActivity;
import com.vcspace.actions.ActionData;

public class CloseAllAction extends FileTabBaseAction {

  @Override
  public void performAction(@NonNull ActionData data) {
    var main = getActivity(data);
    main.closeAllFiles();

    main.invalidateOptionsMenu();
  }

  @Override
  public int getTitle() {
    return R.string.close_all;
  }
}
