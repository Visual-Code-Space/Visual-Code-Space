package com.vcspace.actions;

import android.view.MenuItem;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

public class Presentation {

  private String title;
  private int icon;

  private boolean visible;
  private boolean enabled = true;

  private int showAsAction;

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

  public String getTitle() {
    return this.title;
  }

  public void setTitle(@NonNull String title) {
    this.title = title;
  }

  @DrawableRes
  public int getIcon() {
    return this.icon;
  }

  public void setIcon(@DrawableRes int icon) {
    this.icon = icon;
  }

  public boolean isVisible() {
    return this.visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
