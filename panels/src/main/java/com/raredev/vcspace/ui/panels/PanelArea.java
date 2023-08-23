package com.raredev.vcspace.ui.panels;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.PopupMenu;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.tabs.TabLayout;
import com.raredev.vcspace.events.PanelEvent;
import com.raredev.vcspace.res.R;
import com.raredev.vcspace.res.databinding.LayoutTabItemBinding;
import com.raredev.vcspace.ui.panels.databinding.LayoutPanelAreaBinding;
import com.raredev.vcspace.util.Utils;
import java.util.LinkedList;
import java.util.List;

public class PanelArea implements TabLayout.OnTabSelectedListener {

  protected Context context;
  protected FrameLayout parent;

  protected List<Panel> panels = new LinkedList<>();

  protected LayoutPanelAreaBinding binding;
  protected Panel selectedPanel;

  protected Panel2PanelArea panel2PanelArea;
  protected PanelAreaListener listener;

  public PanelArea(Context context, FrameLayout parent) {
    this.context = context;
    this.parent = parent;
    init();
  }

  private void init() {
    binding = LayoutPanelAreaBinding.inflate(LayoutInflater.from(context));
    binding.tabs.addOnTabSelectedListener(this);

    panel2PanelArea =
        new Panel2PanelArea() {
          @Override
          public PanelArea getPanelArea() {
            return PanelArea.this;
          }

          @Override
          public void romoveThis(Panel panel) {
            removePanel(panel);
          }
        };

    parent.addView(binding.getRoot());
  }

  @Override
  public void onTabReselected(TabLayout.Tab tab) {
    if (listener != null) {
      listener.createTabPopupMenu(panels.get(tab.getPosition()), tab.view).show();
    }
  }

  @Override
  public void onTabSelected(TabLayout.Tab tab) {
    TextView tabTitle = tab.getCustomView().findViewById(R.id.title);
    tabTitle.setTextColor(
        MaterialColors.getColor(context, com.google.android.material.R.attr.colorPrimary, 0));

    int position = tab.getPosition();
    Panel panel = panels.get(position);
    if (panel != null) {
      setSelectedPanel(panel);
    }
  }

  @Override
  public void onTabUnselected(TabLayout.Tab tab) {
    TextView tabTitle = tab.getCustomView().findViewById(R.id.title);
    tabTitle.setTextColor(
        MaterialColors.getColor(context, com.google.android.material.R.attr.colorControlNormal, 0));
  }

  public void addPanelTopBarButtons() {
    int padding = Utils.pxToDp(2);
    ImageView add = new ImageView(context);
    add.setLayoutParams(new ViewGroup.LayoutParams(Utils.pxToDp(25), Utils.pxToDp(25)));
    add.setImageResource(R.drawable.ic_add);
    add.setPadding(padding, padding, padding, padding);
    add.setOnClickListener(
        v -> {
          PopupMenu pm = new PopupMenu(context, v);
          SubMenu addPanelMenu = pm.getMenu().addSubMenu(R.string.add_panel);
          if (listener != null) {
            listener.addAvailablePanels(this, addPanelMenu);
          }
          pm.show();
        });

    addViewInTopbar(add);
  }

  public void sendEvent(PanelEvent event) {
    for (Panel panel : panels) {
      panel.receiveEvent(event);
    }
  }

  public void setSelectedPanel(Panel panel) {
    int position = panels.indexOf(panel);
    TabLayout.Tab tab = binding.tabs.getTabAt(position);
    if (tab != null && !tab.isSelected()) {
      tab.select();
    }
    Panel oldCurrentPanel = selectedPanel;
    if (oldCurrentPanel != null) {
      oldCurrentPanel.setUnselected();
    }
    binding.panelContainer.setDisplayedChild(position);
    panel.setSelected();

    if (listener != null) {
      listener.selectedPanel(panel);
    }
    selectedPanel = panel;
  }

  public void addPanel(Panel panel, boolean select) {
    if (panel.getContentView() == null) {
      return;
    }
    panel.setPanel2PanelArea(panel2PanelArea);
    panels.add(panel);
    binding.tabs.addTab(createTabItem(panel.getTitle()));
    binding.panelContainer.addView(panel.getContentView());
    if (select) setSelectedPanel(panel);
    toggleEmptyPanels(false);
    updateTabs();

    if (listener != null) listener.addPanel(panel);
  }

  private TabLayout.Tab createTabItem(String title) {
    var bind = LayoutTabItemBinding.inflate(LayoutInflater.from(context));
    var tab = binding.tabs.newTab();

    bind.title.setText(title);

    bind.close.setOnClickListener(
        v -> {
          var panel = panels.get(tab.getPosition());
          if (panel.isPinned()) {
            panel.setPinned(false);
            updateTab(tab, panel);
            return;
          }
          removePanel(panels.get(tab.getPosition()));
        });

    tab.setCustomView(bind.getRoot());
    return tab;
  }

  public boolean removePanel(Panel panel) {
    if (panel != null && panels.contains(panel)) {
      if (panel.isPinned()) return false;
      int index = panels.indexOf(panel);

      panel.setDestroyed();
      panels.remove(panel);
      binding.tabs.removeTabAt(index);
      binding.panelContainer.removeViewAt(index);

      if (listener != null) {
        listener.removedPanel(panel);
      }

      if (panels.isEmpty()) {
        toggleEmptyPanels(true);
        selectedPanel = null;
      }
      updateTabs();
      return true;
    }
    return false;
  }

  public void removeOthers() {
    if (panels.isEmpty()) {
      return;
    }
    if (selectedPanel == null) return;

    List<Panel> panelsToClose = new LinkedList<>();
    for (Panel panel : panels) {
      if (panel != null && !panel.equals(selectedPanel) && !panel.isPinned()) {
        panelsToClose.add(panel);
      }
    }

    for (Panel closePanel : panelsToClose) {
      removePanel(closePanel);
    }
    setSelectedPanel(selectedPanel);
  }

  public void removeAllPanels() {
    if (panels.isEmpty()) {
      return;
    }

    List<Panel> panelsToClose = new LinkedList<>();
    for (Panel panel : panels) {
      if (panel != null && !panel.isPinned()) {
        panelsToClose.add(panel);
      }
    }

    for (Panel closePanel : panelsToClose) {
      removePanel(closePanel);
    }
  }

  public <T> T getPanel(Class<T> panelClass) {
    for (Panel panel : panels) {
      if (panel.getClass().getName().equals(panelClass.getName())) {
        return panelClass.cast(panel);
      }
    }
    return null;
  }

  public boolean containsPanel(Class<?> panel) {
    for (Panel temp : panels) {
      if (temp.getClass().getName().equals(panel.getName())) {
        return true;
      }
    }
    return false;
  }

  public void toggleEmptyPanels(boolean isEmpty) {
    binding.viewFlipper.setDisplayedChild(isEmpty ? 1 : 0);
  }

  public Panel getPanel(int position) {
    return panels.get(position);
  }

  public Panel getSelectedPanel() {
    return selectedPanel;
  }

  public TabLayout getTabLayout() {
    return binding.tabs;
  }

  public List<Panel> getPanels() {
    return panels;
  }

  public void addViewInTopbar(View view) {
    binding.topBarButtons.addView(view);
  }

  public void setPanelAreaListener(PanelAreaListener listener) {
    this.listener = listener;
  }

  public void updateTabs() {
    for (int i = 0; i < panels.size(); i++) {
      var tab = binding.tabs.getTabAt(i);
      var panel = panels.get(i);
      if (tab != null && panel != null) {
        updateTab(tab, panel);
      }
    }
  }

  private void updateTab(TabLayout.Tab tab, Panel panel) {
    ImageView close = tab.getCustomView().findViewById(R.id.close);
    close.setImageResource(panel.isPinned() ? R.drawable.ic_pin : R.drawable.close);
  }
}
