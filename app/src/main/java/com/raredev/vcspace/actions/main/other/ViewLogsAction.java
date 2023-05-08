package com.raredev.vcspace.actions.main.other;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.raredev.vcspace.activity.LogViewActivity;
import com.vcspace.actions.ActionData;

public class ViewLogsAction extends MainBaseAction {

  @Override
  public void performAction(@NonNull ActionData data, MenuItem item) {
    var main = getActivity(data);
    main.startActivity(new Intent(main, LogViewActivity.class));
  }

  @Override
  public String getActionId() {
    return "view.logs.action";
  }

  @Override
  public String getTitle(Context context) {
    return context.getString(R.string.menu_view_logs);
  }

  @Override
  public int getIcon() {
    return R.drawable.file_document_outline;
  }
}
