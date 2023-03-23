package com.raredev.vcspace.actions;

import androidx.annotation.NonNull;

public abstract class Action {

  private Presentation presentation = new Presentation();

  private ActionEvent event;

  public void update(@NonNull ActionEvent event) {
    this.event = event;
  }

  public final Presentation getPresentation() {
    return presentation;
  }

  public final ActionEvent getActionEvent() {
    return event;
  }

  public abstract void performAction();
}
