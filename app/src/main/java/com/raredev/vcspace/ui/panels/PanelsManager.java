package com.raredev.vcspace.ui.panels;

import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.widget.PopupMenu;
import com.blankj.utilcode.util.KeyboardUtils;
import com.raredev.vcspace.activities.EditorActivity;
import com.raredev.vcspace.events.PanelEvent;
import com.raredev.vcspace.res.R;
import com.raredev.vcspace.ui.panels.compiler.WebViewPanel;
import com.raredev.vcspace.ui.panels.editor.WelcomePanel;
import com.raredev.vcspace.ui.panels.file.FileExplorerPanel;
import com.raredev.vcspace.utils.Logger;
import java.util.LinkedList;
import java.util.List;

public class PanelsManager {

  private final Logger logger = Logger.newInstance("PanelsManager");
  private final List<FloatingPanelArea> floatingPanels = new LinkedList<>();
  private PanelArea panelArea;

  private EditorActivity activity;
  private FrameLayout parent;
  
  public PanelsManager(EditorActivity activity, FrameLayout parent) {
    this.activity = activity;
    this.parent = parent;

    panelArea = new PanelArea(activity, parent);
    panelArea.addPanelTopBarButtons();
    panelArea.setPanelAreaListener(
        new PanelAreaListener() {
          @Override
          public PopupMenu createTabPopupMenu(Panel panel, View v) {
            final var pm = new PopupMenu(activity, v);
            pm.getMenu().add(R.string.close);
            pm.getMenu().add(R.string.close_others);
            pm.getMenu().add(R.string.close_all);
            pm.getMenu().add(panel.isPinned() ? R.string.unpin : R.string.pin);

            var selectedPanelArea = panel.getPanelArea();
            pm.setOnMenuItemClickListener(
                item -> {
                  if (item.getTitle().equals(activity.getString(R.string.close))) {
                    selectedPanelArea.removePanel(panel);
                  } else if (item.getTitle().equals(activity.getString(R.string.close_others))) {
                    selectedPanelArea.removeOthers();
                  } else if (item.getTitle().equals(activity.getString(R.string.close_all))) {
                    selectedPanelArea.removeAllPanels();
                  } else if (item.getTitle().equals(activity.getString(R.string.pin))
                      || item.getTitle().equals(activity.getString(R.string.unpin))) {
                    panel.setPinned(!panel.isPinned());
                    item.setTitle(panel.isPinned() ? R.string.unpin : R.string.pin);
                    selectedPanelArea.updateTabs();
                  }
                  return true;
                });
            return pm;
          }

          @Override
          public void addAvailablePanels(PanelArea panelArea, Menu menu) {
            menu.add(R.string.welcome)
                .setOnMenuItemClickListener(
                    item -> {
                      panelArea.addPanel(new WelcomePanel(activity), true);
                      return true;
                    });
            menu.add("File Explorer")
                .setOnMenuItemClickListener(
                    item -> {
                      panelArea.addPanel(new FileExplorerPanel(activity), true);
                      return true;
                    });
            menu.add("WebView")
                .setOnMenuItemClickListener(
                    item -> {
                      panelArea.addPanel(new WebViewPanel(activity), true);
                      return true;
                    });
          }

          @Override
          public void addPanel(Panel panel) {
            activity.savePanels();
          }

          @Override
          public void selectedPanel(Panel panel) {
            activity.updateCurrentPanel(panel);
          }

          @Override
          public void removedPanel(PanelArea panelArea) {
            PanelsManager.this.removePanelArea(panelArea);
          }

          @Override
          public void removedPanel(Panel panel) {
            activity.invalidateOptionsMenu();
            activity.savePanels();
          }
        });
  }

  public void sendEvent(PanelEvent event) {
    panelArea.sendEvent(event);
    for (FloatingPanelArea floatingPanel : floatingPanels) {
      floatingPanel.sendEvent(event);
    }
    logger.i("PanelEvent: " + event.getClass().getSimpleName()+". sent!");
  }

  public void addDefaultPanels() {
    if (panelArea.getPanels().isEmpty()) {
      addPanel(new WelcomePanel(activity), true);
      addPanel(new FileExplorerPanel(activity), false);
    }
  }

  public void addPanel(Panel panel, boolean select) {
    panelArea.addPanel(panel, select);
  }

  public Panel getSelectedPanel() {
    return panelArea.getSelectedPanel();
  }

  public List<Panel> getPanelAreaPanels() {
    return panelArea.getPanels();
  }

  public void addFloatingPanel(FloatingPanelArea floatingPanel) {
    floatingPanels.add(floatingPanel);
  }

  public void removePanelArea(PanelArea panelArea) {
    if (floatingPanels.contains(panelArea)) {
      floatingPanels.remove(panelArea);
    }
  }

  public void removePanel(Panel panel) {
    if (panel != null && !panelArea.removePanel(panel)) {
      for (FloatingPanelArea floatingPanel : floatingPanels) {
        floatingPanel.removePanel(panel);
      }
    }
  }

  public void removeOthers(PanelArea area) {
    area.removeOthers();
  }

  public void removeAllPanels(PanelArea area) {
    KeyboardUtils.hideSoftInput(activity);
    area.removeAllPanels();
  }

  public <T> T getFloatingPanel(Class<T> panelClass) {
    for (FloatingPanelArea panel : floatingPanels) {
      if (panel.getClass().getName().equals(panelClass.getName())) {
        return panelClass.cast(panel);
      }
    }
    return null;
  }

  public Panel getPanel(int position) {
    return panelArea.getPanel(position);
  }

  public List<FloatingPanelArea> getFloatingPanels() {
    return floatingPanels;
  }

  public PanelArea getPanelArea() {
    return panelArea;
  }

  public void addWebViewPanel(String path) {
    WebViewPanel webViewPanel = getPanelArea().getPanel(WebViewPanel.class);
    if (webViewPanel == null) {
      webViewPanel = new WebViewPanel(activity);
      addPanel(webViewPanel, false);
    }
    webViewPanel.loadFile(path);
    panelArea.setSelectedPanel(webViewPanel);
  }
}
