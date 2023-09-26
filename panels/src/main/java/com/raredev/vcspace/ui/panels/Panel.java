package com.raredev.vcspace.ui.panels;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import com.raredev.vcspace.events.PanelEvent;

public abstract class Panel {

  private String title;
  private boolean pinned;

  private Context context;

  private View contentView;
  private Panel2PanelArea panel2PanelArea;

  boolean selected;
  boolean viewCreated;
  boolean destroyed;

  public Panel(Context context, int title) {
    this(context, context.getString(title));
  }

  public Panel(Context context, String title) {
    this.context = context;
    this.title = title;

    this.selected = false;
    this.pinned = false;
    this.viewCreated = false;
    this.destroyed = false;
  }

  void setPanel2PanelArea(Panel2PanelArea panel2PanelArea) {
    this.panel2PanelArea = panel2PanelArea;
  }

  void performSelected() {
    if (!selected && !destroyed && viewCreated) {
      selected = true;
      selected();
    }
  }

  void performUnselected() {
    if (selected && !destroyed && viewCreated) {
      selected = false;
      unselected();
    }
  }

  void performCreateView(LayoutInflater inflater) {
    if (!viewCreated) {
      contentView = createView(inflater);
      viewCreated = true;

      viewCreated(contentView);
    }
  }

  void performDestroy() {
    if (viewCreated) {
      destroy();
      viewCreated = false;
      contentView = null;
      destroyed = true;
    }
  }

  public void updateTitle(String title) {
    panel2PanelArea.updateTitle(title, this);
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  public boolean isSelected() {
    return selected;
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

  public View createView(LayoutInflater inflater) {
    return null;
  }

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
