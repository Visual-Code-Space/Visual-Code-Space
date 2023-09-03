package com.raredev.vcspace.ui.panels;

import android.content.Context;
import android.view.Menu;
import android.view.View;
import com.raredev.vcspace.events.PanelEvent;

public abstract class Panel {

  private String title;
  private boolean pinned;

  private Context context;

  private View contentView;
  private Panel2PanelArea panel2PanelArea;

  boolean viewCreated;
  boolean destroyed;

  public Panel(Context context) {
    this.context = context;

    this.pinned = false;
    this.viewCreated = false;
    this.destroyed = false;
  }

  void setPanel2PanelArea(Panel2PanelArea panel2PanelArea) {
    this.panel2PanelArea = panel2PanelArea;
  }

  void performSelected() {
    if (!destroyed && viewCreated) selected();
  }

  void performUnselected() {
    if (!destroyed && viewCreated) unselected();
  }

  void performCreateView() {
    if (!viewCreated) {
      contentView = createView();
      viewCreated = true;

      viewCreated(contentView);
    }
  }

  void performDestroy() {
    if (viewCreated) {
      viewCreated = false;
      contentView = null;
      destroyed = true;
      destroy();
    }
  }

  public void updateTitle(String title) {
    panel2PanelArea.updateTitle(title, this);
    setTitle(title);
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

  public View createView() {
    return null;
  }

  public void createPanelMenu(Menu menu) {}

  public void viewCreated(View view) {}

  public void receiveEvent(PanelEvent event) {}

  public void updatePanelTab() {}

  public boolean isViewCreated() {
    return viewCreated;
  }

  public abstract void unselected();

  public abstract void selected();

  public abstract void destroy();
}
