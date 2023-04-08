package com.raredev.vcspace.actions.main.filetoolbar;

import androidx.annotation.NonNull;
import com.raredev.vcspace.R;
import com.vcspace.actions.ActionData;
import com.raredev.vcspace.actions.main.MainBaseAction;

public class OpenFileAction extends MainBaseAction {

  @Override
  public void performAction(@NonNull ActionData data) {
    getActivity(data).pickFile.launch("text/*");
  }

  @Override
  public int getTitle() {
    return R.string.menu_open_file;
  }

  @Override
  public int getIcon() {
    return R.drawable.ic_file;
  }
}
