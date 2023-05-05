package com.raredev.vcspace.events;

public class PreferenceChangedEvent {

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
