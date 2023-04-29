package com.raredev.vcspace.actions.main.filetab;

import android.content.Context;
import androidx.annotation.NonNull;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.main.FileTabBaseAction;
import com.vcspace.actions.ActionData;

public class CloseAllAction extends FileTabBaseAction {

  @Override
  public void performAction(@NonNull ActionData data) {
    getActivity(data).closeAllFiles();
  }

  @Override
  public String getActionId() {
    return "close.all.action";
  }

  @Override
  public String getTitle(Context context) {
    return context.getString(R.string.close_all);
  }
}
