package com.raredev.vcspace.actions;

import androidx.annotation.NonNull;

public abstract class Action {

  public int title;
  public int icon;

  public boolean visible;
  public boolean enabled = true;

  public Location location;

  public void update(@NonNull ActionData data) {}

  public abstract void performAction(@NonNull ActionData data);

  public enum Location {
    MAIN_TOOLBAR,
    
    EDITOR,
    FILE_TREE;
  }
}
