package com.vcspace.actions.impl;

import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import androidx.annotation.NonNull;
import com.vcspace.actions.Action;
import com.vcspace.actions.ActionData;
import com.vcspace.actions.ActionGroup;
import com.vcspace.actions.ActionManager;
import java.util.LinkedHashMap;
import java.util.Map;

public class ActionManagerImpl extends ActionManager {

  private Map<String, Action> actions = new LinkedHashMap<>();

  @Override
  public void fillMenu(Menu menu, ActionData data, String location) {
    for (Action action : actions.values()) {
      action.update(data);
      
      if (action.location == location && action.visible) {
        fillMenu(menu, action, data);
      }
    }
  }

  private void fillMenu(Menu menu, Action action, ActionData data) {
    MenuItem menuItem;
    if (action instanceof ActionGroup) {
      SubMenu subMenu = menu.addSubMenu(action.title);
      subMenu.setIcon(action.icon);

      Action[] children = ((ActionGroup) action).getChildren(data);
      if (children != null) {
        for (Action child : children) {
          child.update(data);
          fillMenu(subMenu, child, data);
        }
      }
      menuItem = subMenu.getItem();
    } else {
      menuItem = menu.add(action.title);
    }

    if (action.icon != -1) {
      menuItem.setIcon(action.icon);
    }
    menuItem.setEnabled(action.enabled);
    menuItem.setShowAsAction(action.getShowAsAction());

    menuItem.setOnMenuItemClickListener(
        item -> {
          action.performAction(data);
          return true;
        });
  }

  @Override
  public void registerAction(@NonNull String id, @NonNull Action action) {
    actions.put(id, action);
  }

  @Override
  public void registerAction(@NonNull Action action) {
    actions.put(action.getClass().getName(), action);
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
