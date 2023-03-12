package com.raredev.vcspace.ui.editor;

import com.raredev.vcspace.util.PreferencesUtils;

public class Symbol {
  private String label;
  private String insert;

  public Symbol(String label) {
    this(label, label);
  }

  public Symbol(String label, String insert) {
    this.label = label;
    this.insert = insert;
  }

  public String getLabel() {
    return this.label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getInsert() {
    return this.insert;
  }

  public void setInsert(String insert) {
    this.insert = insert;
  }
}
