package com.vcspace.actions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class ActionGroup extends Action {

  @Override
  public void performAction(@NonNull ActionData data) {}

  public abstract Action[] getChildren(@Nullable ActionData data);
}
