package com.raredev.vcspace.ui.panels;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.raredev.vcspace.events.PanelEvent;

public abstract class Panel {

  private String title;
  private boolean pinned;

  private Context context;
  private View contentView;

  Panel2PanelArea panel2PanelArea;
  boolean destroyed;

  public Panel(Context context) {
    this.context = context;

    this.pinned = false;
  }

  void setPanel2PanelArea(Panel2PanelArea panel2PanelArea) {
    this.panel2PanelArea = panel2PanelArea;
  }

  void setSelected() {
    if (!destroyed) selected();
  }

  void setUnselected() {
    if (!destroyed) unselected();
  }

  void setDestroyed() {
    destroyed = true;
    destroy();
  }

  public void receiveEvent(PanelEvent event) {}

  public abstract void unselected();

  public abstract void selected();

  public abstract void destroy();

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

  public PanelArea getPanelArea() {
    return panel2PanelArea.getPanelArea();
  }

  public void removeThis() {
    panel2PanelArea.romoveThis(this);
  }

  public View getContentView() {
    return contentView;
  }

  public Context getContext() {
    return this.context;
  }
}
