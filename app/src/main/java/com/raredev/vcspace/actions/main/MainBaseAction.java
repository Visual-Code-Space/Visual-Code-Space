package com.raredev.vcspace.actions.main;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import com.raredev.vcspace.actions.Action;
import com.raredev.vcspace.actions.ActionData;

public abstract class MainBaseAction extends Action {

  public MainBaseAction() {
    location = Action.Location.MAIN_TOOLBAR;
  }

  @Override
  public void update(@NonNull ActionData data) {
    visible = true;
    title = getTitle();
    icon = getIcon();
  }

  @StringRes
  public abstract int getTitle();
  
  @StringRes
  public abstract int getIcon();
}
