package com.raredev.vcspace.actions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class ActionGroup extends Action {

  @Override
  public void performAction(@NonNull ActionData data) {}

  public boolean isPopup() {
    return true;
  }

  public abstract Action[] getChildren(@Nullable ActionData data);
}
