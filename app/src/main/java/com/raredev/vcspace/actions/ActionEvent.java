package com.raredev.vcspace.actions;

import androidx.annotation.Nullable;

public class ActionEvent {

  private ActionData data;

  private Presentation presentation;

  private String place;

  public ActionEvent(ActionData data, Presentation presentation, String place) {
    this.data = data;
    this.presentation = presentation;
    this.place = place;
  }

  public String getPlace() {
    return place;
  }

  public Presentation getPresentation() {
    return presentation;
  }

  @Nullable
  public Object getData(String key) {
    return data.get(key);
  }
}
