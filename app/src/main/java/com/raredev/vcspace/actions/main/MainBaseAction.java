package com.raredev.vcspace.actions.main;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import com.raredev.vcspace.activity.MainActivity;
import com.vcspace.actions.Action;
import com.vcspace.actions.ActionData;
import com.vcspace.actions.location.DefaultLocations;

public abstract class MainBaseAction extends Action {

  public MainBaseAction() {
    this.location = DefaultLocations.MAIN_TOOLBAR;
  }

  @Override
  public void update(@NonNull ActionData data) {
    visible = true;
    var main = getActivity(data);
    if (main == null) {
      return;
    }
    title = main.getString(getTitle());
    icon = getIcon();
  }

  @StringRes
  public abstract int getTitle();

  @StringRes
  public abstract int getIcon();

  public MainActivity getActivity(ActionData data) {
    return (MainActivity) data.get(MainActivity.class);
  }
}
