package com.raredev.vcspace.models;

public class OptionModel {
  private int icon;
  private String name;

  public OptionModel(int icon, String name) {
    this.icon = icon;
    this.name = name;
  }

  public int getIcon() {
    return this.icon;
  }

  public void setIcon(int icon) {
    this.icon = icon;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
