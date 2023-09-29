package com.raredev.vcspace.ui.panels;

import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.widget.PopupMenu;
import com.raredev.vcspace.activities.EditorActivity;
import com.raredev.vcspace.events.PanelEvent;
import com.raredev.vcspace.res.R;
import com.raredev.vcspace.ui.panels.editor.EditorPanelArea;
import com.raredev.vcspace.ui.panels.file.FileExplorerPanel;
import com.raredev.vcspace.ui.panels.web.WebViewPanel;
import com.raredev.vcspace.ui.panels.welcome.WelcomePanel;
import com.raredev.vcspace.utils.Logger;
import java.util.LinkedList;
import java.util.List;

public class PanelsManager {

  private final Logger logger = Logger.newInstance("PanelsManager");
  private final List<FloatingPanelArea> floatingPanels = new LinkedList<>();

  private EditorActivity activity;

  private FrameLayout workspaceParent;
  private PanelArea workspaceArea;

  private FrameLayout parent;
  private EditorPanelArea panelArea;

  public PanelsManager(EditorActivity activity, FrameLayout workspaceParent, FrameLayout parent) {
    this.activity = activity;
    this.workspaceParent = workspaceParent;
    this.parent = parent;

    workspaceArea = new PanelArea(activity, workspaceParent);
    workspaceArea.setFixedPanels(true);
    panelArea = new EditorPanelArea(activity, parent);
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
          public void addAvailablePanels(PanelArea panelArea, Menu menu) {}

          @Override
          public void addPanel(Panel panel) {}

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
            activity.onPanelRemoved();
          }
        });
  }

  public void sendEvent(PanelEvent event) {
    panelArea.sendEvent(event);
    workspaceArea.sendEvent(event);
    for (FloatingPanelArea floatingPanel : floatingPanels) {
      floatingPanel.sendEvent(event);
    }
    logger.i("PanelEvent: " + event.getClass().getSimpleName() + ". sent!");
  }

  public void addDefaultPanels() {
    if (workspaceArea.getPanels().isEmpty()) {
      addPanelInWorkspace(new FileExplorerPanel(activity), true);
    }
    if (panelArea.getPanels().isEmpty()) {
      addPanel(new WelcomePanel(activity), true);
    }
  }

  public void addPanelInWorkspace(Panel panel, boolean select) {
    workspaceArea.addPanel(panel, select);
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

  public EditorPanelArea getPanelArea() {
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
