package com.raredev.vcspace.events;

public class PreferenceChangedEvent extends PanelEvent {

  private String key;

  public PreferenceChangedEvent(String key) {
    this.key = key;
  }

  public String getKey() {
    return this.key;
  }

  public void setKey(String key) {
    this.key = key;
  }
}
