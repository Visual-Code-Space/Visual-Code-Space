package com.raredev.vcspace.actions.main.other;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.raredev.vcspace.activity.SettingsActivity;
import com.vcspace.actions.ActionData;

public class SettingsAction extends MainBaseAction {

  @Override
  public void performAction(@NonNull ActionData data) {
    var main = getActivity(data);
    main.startActivity(new Intent(main, SettingsActivity.class));
  }

  @Override
  public String getActionId() {
    return "settings.action";
  }

  @Override
  public String getTitle(Context context) {
    return context.getString(R.string.menu_settings);
  }

  @Override
  public int getIcon() {
    return R.drawable.ic_settings;
  }
}
