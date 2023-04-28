package com.raredev.vcspace.fragments.filemanager.actions.topbar;

import android.content.Context;
import androidx.annotation.NonNull;
import com.raredev.vcspace.R;
import com.raredev.vcspace.fragments.filemanager.FileManagerFragment;
import com.raredev.vcspace.fragments.filemanager.actions.TopbarBaseAction;
import com.vcspace.actions.ActionData;

public class RefreshFilesAction extends TopbarBaseAction {

  @Override
  public void performAction(@NonNull ActionData data) {
    FileManagerFragment fragment = getFragment(data);
    fragment.refreshFiles();
  }

  @Override
  public String getActionId() {
    return "refresh.files.action";
  }

  @Override
  public String getTitle(Context context) {
    return context.getString(R.string.refresh);
  }

  @Override
  public int getIcon() {
    return R.drawable.ic_refresh;
  }
}
