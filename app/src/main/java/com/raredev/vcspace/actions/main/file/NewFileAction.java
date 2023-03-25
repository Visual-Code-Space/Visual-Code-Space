package com.raredev.vcspace.actions.main.file;

import androidx.annotation.NonNull;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.ActionData;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.raredev.vcspace.activity.MainActivity;

public class NewFileAction extends MainBaseAction {

  @Override
  public void performAction(@NonNull ActionData data) {
    ((MainActivity) data.get(MainActivity.class)).createFile.launch("untitled");
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
