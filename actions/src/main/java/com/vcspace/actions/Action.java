package com.vcspace.actions;

import android.view.MenuItem;
import androidx.annotation.NonNull;

public abstract class Action {

  private Presentation presentation = createDefaultPresentation();

  public abstract void update(@NonNull ActionData data);

  public abstract void performAction(@NonNull ActionData data, MenuItem item);

  public abstract String getActionId();
  
  public abstract String getLocation();

  public Presentation getPresentation() {
    return presentation;
  }

  private Presentation createDefaultPresentation() {
    Presentation presentation = new Presentation();
    presentation.setTitle("");
    presentation.setVisible(true);
    presentation.setEnabled(true);
    return presentation;
  }
}
