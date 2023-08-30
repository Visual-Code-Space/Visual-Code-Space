package com.raredev.vcspace.ui.panels;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.raredev.vcspace.events.PanelEvent;

public abstract class Panel {

  private String title;
  private boolean pinned;

  private Context context;

  View contentView;
  Panel2PanelArea panel2PanelArea;
  boolean destroyed;

  public Panel(Context context) {
    this.context = context;

    this.pinned = false;
    this.destroyed = false;
  }

  void setPanel2PanelArea(Panel2PanelArea panel2PanelArea) {
    this.panel2PanelArea = panel2PanelArea;
  }

  void performSelected() {
    if (!destroyed) selected();
  }

  void performUnselected() {
    if (!destroyed) unselected();
  }

  void performDestroy() {
    destroyed = true;
    destroy();
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  public boolean isPinned() {
    return this.pinned;
  }

  public void setPinned(boolean pinned) {
    this.pinned = pinned;
  }

  public void setContentView(View view) {
    view.setLayoutParams(
        new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    contentView = view;
  }

  public View getContentView() {
    return contentView;
  }

  public Context getContext() {
    return this.context;
  }
  
  public PanelArea getPanelArea() {
    return panel2PanelArea.getPanelArea();
  }

  public void removeThis() {
    panel2PanelArea.romoveThis(this);
  }

  public void receiveEvent(PanelEvent event) {}

  public abstract void unselected();

  public abstract void selected();

  public abstract void destroy();
}
