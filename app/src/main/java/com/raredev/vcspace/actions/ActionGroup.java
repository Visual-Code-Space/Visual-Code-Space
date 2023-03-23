package com.raredev.vcspace.actions;

import androidx.annotation.Nullable;

public abstract class ActionGroup extends Action {

  @Override
  public void performAction() {}

  public boolean isPopup() {
    return true;
  }

  public abstract Action[] getChildren(@Nullable ActionEvent event);
}
