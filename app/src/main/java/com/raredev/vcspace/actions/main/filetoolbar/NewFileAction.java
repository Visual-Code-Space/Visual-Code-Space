package com.raredev.vcspace.actions.main.filetoolbar;

import android.content.Context;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.vcspace.actions.ActionData;

public class NewFileAction extends MainBaseAction {

  @Override
  public void performAction(@NonNull ActionData data, MenuItem item) {
    getActivity(data).createFile.launch("untitled");
  }

  @Override
  public String getActionId() {
    return "new.file.action";
  }

  @Override
  public String getTitle(Context context) {
    return context.getString(R.string.menu_new_file);
  }

  @Override
  public int getIcon() {
    return R.drawable.ic_add;
  }
}
