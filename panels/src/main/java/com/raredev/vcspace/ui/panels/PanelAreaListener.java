package com.raredev.vcspace.ui.panels;

import android.view.Menu;
import android.view.View;
import androidx.appcompat.widget.PopupMenu;

public interface PanelAreaListener {

  PopupMenu createTabPopupMenu(Panel panel, View v);

  void addAvailablePanels(PanelArea panelArea, Menu menu);

  void addPanel(Panel panel);

  void selectedPanel(Panel panel);

  void removedPanel(PanelArea panelArea);

  void removedPanel(Panel panel);
}
