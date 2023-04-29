package com.raredev.vcspace.actions.main.filetoolbar;

import android.content.Context;
import androidx.annotation.NonNull;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.vcspace.actions.ActionData;

public class OpenFileAction extends MainBaseAction {

  @Override
  public void performAction(@NonNull ActionData data) {
    getActivity(data).pickFile.launch("text/*");
  }

  @Override
  public String getActionId() {
    return "open.file.action";
  }

  @Override
  public String getTitle(Context context) {
    return context.getString(R.string.menu_open_file);
  }

  @Override
  public int getIcon() {
    return R.drawable.ic_file;
  }
}
