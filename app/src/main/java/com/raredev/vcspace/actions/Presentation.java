package com.raredev.vcspace.actions;

import androidx.annotation.StringRes;

public class Presentation {

  private int title;
  private int icon;

  private boolean visible;

  public Presentation() {}

  public int getTitle() {
    return this.title;
  }

  public void setTitle(@StringRes int title) {
    this.title = title;
  }

  public int getIcon() {
    return this.icon;
  }

  public void setIcon(int icon) {
    this.icon = icon;
  }

  public boolean isVisible() {
    return this.visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }
}
