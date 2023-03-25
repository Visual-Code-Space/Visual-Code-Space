package com.raredev.vcspace.actions;

import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import androidx.fragment.app.FragmentManager;
import com.raredev.vcspace.fragments.OptionsSheetFragment;
import java.util.HashMap;
import java.util.Map;

public class ActionManager {
  private Map<Integer, Action> actions = new HashMap<>();

  private static ActionManager actionManager;

  public static ActionManager getInstance() {
    if (actionManager == null) {
      actionManager = new ActionManager();
    }
    return actionManager;
  }

  private ActionManager() {}

  public void registerAction(Action action) {
    actions.put(actions.size(), action);
  }

  public Action getAction(int id) {
    return actions.get(id);
  }

  public void clear() {
    actions.clear();
  }

  public void fillMenu(Menu menu, ActionData data, Action.Location location) {
    for (Action action : actions.values()) {
      action.update(data);
      if (action.visible && action.location == location) {
        fillMenu(menu, action, data);
      }
    }
  }

  private void fillMenu(Menu menu, Action action, ActionData data) {
    MenuItem menuItem;
    if (action instanceof ActionGroup) {
      SubMenu subMenu = menu.addSubMenu(action.title);
      subMenu.setIcon(action.icon);

      Action[] children = ((ActionGroup)action).getChildren(data);
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

    if (menuItem.getIcon() != null) {
      menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    } else {
      menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    }
    menuItem.setOnMenuItemClickListener(
        item -> {
          action.performAction(data);
          return true;
        });
  }

  public void fillDialogMenu(
      FragmentManager fragmentManager, ActionData data, Action.Location location) {
    OptionsSheetFragment options = OptionsSheetFragment.createSheet(data);

    for (Action action : actions.values()) {
      action.update(data);

      if (action.visible && action.location == location) {
        options.addAction(action);
      }
    }

    options.show(fragmentManager, "");
  }
}
