package com.vcspace.actions.impl;

import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import androidx.annotation.NonNull;
import com.raredev.vcspace.util.ILogger;
import com.raredev.vcspace.util.ToastUtils;
import com.vcspace.actions.Action;
import com.vcspace.actions.ActionData;
import com.vcspace.actions.ActionGroup;
import com.vcspace.actions.ActionManager;
import com.vcspace.actions.Presentation;
import java.util.LinkedHashMap;
import java.util.Map;

public class ActionManagerImpl extends ActionManager {
  private static final String LOG = "ActionManagerImpl";

  private Map<String, Action> actions = new LinkedHashMap<>();

  @Override
  public void fillMenu(Menu menu, ActionData data, String location) {
    for (Action action : actions.values()) {
      if (action.getLocation().equals(location)) {
        action.update(data);

        fillMenu(menu, action, data);
      }
    }
  }

  private void fillMenu(Menu menu, Action action, ActionData data) {
    Presentation presentation = action.getPresentation();
    if (!presentation.isVisible()) {
      return;
    }

    MenuItem menuItem;
    if (action instanceof ActionGroup) {
      SubMenu subMenu = menu.addSubMenu(presentation.getTitle());

      Action[] children = ((ActionGroup) action).getChildren(data);
      if (children != null) {
        for (Action child : children) {
          child.update(data);
          fillMenu(subMenu, child, data);
        }
      }
      menuItem = subMenu.getItem();
    } else {
      menuItem = menu.add(presentation.getTitle());
    }

    if (presentation.getIcon() != -1) {
      menuItem.setIcon(presentation.getIcon());
    }
    menuItem.setEnabled(presentation.isEnabled());
    menuItem.setCheckable(presentation.isCheckable());
    menuItem.setChecked(presentation.isChecked());
    menuItem.setShowAsAction(presentation.getShowAsAction());

    if (!(action instanceof ActionGroup)) {
      menuItem.setOnMenuItemClickListener(item -> performAction(action, data, item));
    }
  }

  @Override
  public void registerAction(@NonNull String id, @NonNull Action action) {
    actions.put(id, action);
  }

  @Override
  public void registerAction(@NonNull Action action) {
    actions.put(action.getActionId(), action);
  }

  @Override
  public void performAction(@NonNull String id, @NonNull ActionData data, MenuItem item) {
    Action action = actions.get(id);
    performAction(action, data, item);
  }

  private boolean performAction(@NonNull Action action, @NonNull ActionData data, MenuItem item) {
    try {
      action.performAction(data, item);
      return true;
    } catch (Throwable e) {
      ToastUtils.showShort("Unable to perform action", ToastUtils.TYPE_ERROR);
      ILogger.error(LOG, "Unable to perform action", e);
      return false;
    }
  }

  @Override
  public void unregisterAction(Action action) {
    actions.remove(action);
  }

  @Override
  public void unregisterAction(@NonNull String actionId) {
    actions.remove(actions.get(actionId));
  }

  @Override
  public Map<String, Action> getActions() {
    return actions;
  }

  @Override
  public void clear() {
    actions.clear();
  }
}
