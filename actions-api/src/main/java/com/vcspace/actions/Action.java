package com.vcspace.actions;

import android.view.MenuItem;
import androidx.annotation.NonNull;

public abstract class Action {

  public String title;
  public int icon;

  public boolean visible;
  public boolean enabled = true;

  private int showAsAction;
  public String location;

  public abstract void update(@NonNull ActionData data);

  public abstract void performAction(@NonNull ActionData data);

  public int getShowAsAction() {
    if (this.showAsAction == -1) {
      return MenuItem.SHOW_AS_ACTION_NEVER;
    }
    return this.showAsAction;
  }

  public void setShowAsAction(int showAsAction) {
    switch (showAsAction) {
      case MenuItem.SHOW_AS_ACTION_NEVER:
      case MenuItem.SHOW_AS_ACTION_IF_ROOM:
      case MenuItem.SHOW_AS_ACTION_ALWAYS:
      case MenuItem.SHOW_AS_ACTION_WITH_TEXT:
      case MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW:
        this.showAsAction = showAsAction;
        break;
      default:
        this.showAsAction = MenuItem.SHOW_AS_ACTION_NEVER;
    }
  }
}
