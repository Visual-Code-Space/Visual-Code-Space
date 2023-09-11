package com.raredev.vcspace.ui.panels;

import android.content.Context;
import android.view.LayoutInflater;
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
import com.raredev.vcspace.ui.panels.databinding.LayoutPanelAreaBinding;
import com.raredev.vcspace.utils.Logger;
import com.raredev.vcspace.utils.Utils;
import java.util.LinkedList;
import java.util.List;

public class PanelArea implements TabLayout.OnTabSelectedListener {

  protected Context context;
  protected FrameLayout parent;

  protected final Logger logger = Logger.newInstance("PanelArea");
  protected final List<Panel> panels = new LinkedList<>();
  protected final List<Panel> panelsToRemove = new LinkedList<>();

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
    binding.tabs.setTabIndicatorAnimationMode(TabLayout.INDICATOR_ANIMATION_MODE_LINEAR);
    binding.tabs.addOnTabSelectedListener(this);

    panel2PanelArea =
        new Panel2PanelArea() {

          @Override
          public void updateTitle(String title, Panel panel) {
            var tab = binding.tabs.getTabAt(panels.indexOf(panel));
            TextView text = tab.getCustomView().findViewById(R.id.title);
            text.setText(title);
          }

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
      var pm = listener.createTabPopupMenu(panels.get(tab.getPosition()), tab.view);
      if (pm != null) pm.show();
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
    ImageView menu = new ImageView(context);
    menu.setLayoutParams(new ViewGroup.LayoutParams(Utils.pxToDp(25), Utils.pxToDp(25)));
    menu.setImageResource(R.drawable.ic_menu);
    menu.setPadding(padding, padding, padding, padding);
    menu.setOnClickListener(
        v -> {
          PopupMenu pm = new PopupMenu(context, v);
          SubMenu addPanelMenu = pm.getMenu().addSubMenu(R.string.add_panel);
          if (listener != null) {
            listener.addAvailablePanels(this, addPanelMenu);
          }
          pm.show();
        });
    binding.topBarButtons.addView(menu, 0);
  }

  public void sendEvent(PanelEvent event) {
    for (Panel panel : panels) {
      if (!panel.viewCreated) {
        continue;
      }
      panel.receiveEvent(event);
    }
  }

  public void setSelectedPanel(Panel panel) {
    int position = panels.indexOf(panel);
    TabLayout.Tab tab = binding.tabs.getTabAt(position);
    if (tab != null && !tab.isSelected()) {
      tab.select();
    }

    Panel lastSelectedPanel = selectedPanel;
    if (lastSelectedPanel != null) {
      binding.panelContainer.removeView(lastSelectedPanel.getContentView());
      lastSelectedPanel.performUnselected();
    }
    panel.performCreateView();

    binding.panelContainer.addView(panel.getContentView());
    panel.performSelected();

    if (listener != null) {
      listener.selectedPanel(panel);
    }
    logger.i(
        "Panel: "
            + panel.getClass().getSimpleName()
            + ". in position: "
            + position
            + ". selected!");
    selectedPanel = panel;
  }

  public void addPanel(Panel panel, boolean select) {
    panel.setPanel2PanelArea(panel2PanelArea);
    panel.performCreateView();

    panels.add(panel);
    binding.tabs.addTab(createTabItem(panel));

    if (panels.size() == 0 || select) setSelectedPanel(panel);
    switchEmptyPanels();
    updateTabs();

    if (listener != null) listener.addPanel(panel);
  }

  private TabLayout.Tab createTabItem(Panel panel) {
    var tab = binding.tabs.newTab();
    tab.setCustomView(R.layout.layout_tab_item);
    ((TextView) tab.getCustomView().findViewById(R.id.title)).setText(panel.getTitle());
    tab.getCustomView()
        .findViewById(R.id.close)
        .setOnClickListener(
            v -> {
              if (panel.isPinned()) {
                panel.setPinned(false);
                updateTab(tab, panel);
                return;
              }
              removePanel(panel);
            });
    return tab;
  }

  public boolean removePanel(Panel panel) {
    if (panel != null && panels.contains(panel)) {
      if (panel.isPinned()) return false;
      int index = panels.indexOf(panel);

      panels.remove(panel);
      if (panel.viewCreated) {
        binding.panelContainer.removeView(panel.getContentView());
      }
      binding.tabs.removeTabAt(index);
      panel.performDestroy();

      if (listener != null) {
        listener.removedPanel(panel);
      }

      if (panels.isEmpty()) {
        switchEmptyPanels();
        selectedPanel = null;
      }
      updateTabs();
      return true;
    }
    return false;
  }

  public void removeOthers() {
    if (panels.isEmpty() || selectedPanel == null) {
      logger.e("No panels to remove!");
      return;
    }

    for (Panel panel : panels) {
      if (panel != null && !panel.equals(selectedPanel) && !panel.isPinned()) {
        panelsToRemove.add(panel);
      }
    }

    for (Panel closePanel : panelsToRemove) {
      removePanel(closePanel);
    }
    panelsToRemove.clear();
  }

  public void removeAllPanels() {
    if (panels.isEmpty()) {
      logger.e("No panels to remove!");
      return;
    }

    for (Panel panel : panels) {
      if (panel != null && !panel.isPinned()) {
        panelsToRemove.add(panel);
      }
    }

    for (Panel closePanel : panelsToRemove) {
      removePanel(closePanel);
    }
    panelsToRemove.clear();
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

  public void switchEmptyPanels() {
    binding.viewFlipper.setDisplayedChild(panels.isEmpty() ? 1 : 0);
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

        logger.i("Tab at index: " + i + ". updated!");
      }
    }
  }

  private void updateTab(TabLayout.Tab tab, Panel panel) {
    ImageView close = tab.getCustomView().findViewById(R.id.close);
    close.setImageResource(panel.isPinned() ? R.drawable.ic_pin : R.drawable.close);
    panel.updatePanelTab();
  }
}
