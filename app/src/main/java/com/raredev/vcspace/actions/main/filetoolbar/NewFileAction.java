package com.raredev.vcspace.actions.main.filetoolbar;

import androidx.annotation.NonNull;
import com.raredev.vcspace.R;
import com.vcspace.actions.ActionData;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.raredev.vcspace.activity.MainActivity;

public class NewFileAction extends MainBaseAction {

  @Override
  public void performAction(@NonNull ActionData data) {
    getActivity(data).createFile.launch("untitled");
  }

  @Override
  public int getTitle() {
    return R.string.menu_new_file;
  }

  @Override
  public int getIcon() {
    return R.drawable.ic_add;
  }
}
