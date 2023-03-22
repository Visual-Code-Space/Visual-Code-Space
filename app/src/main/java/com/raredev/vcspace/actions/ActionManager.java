package com.raredev.vcspace.actions;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.PopupMenu;
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

  public void fillMenu(Context context, View v, ActionData data, String place) {
    PopupMenu popupMenu = new PopupMenu(context, v);
    for (Action action : actions.values()) {
      ActionEvent event = new ActionEvent(data, action.getPresentation(), place);
      action.update(event);

      if (event.getPresentation().isVisible()) {
        fillMenu(popupMenu.getMenu(), action, event);
      }
    }
    popupMenu.show();
  }

  private void fillMenu(Menu menu, Action action, ActionEvent event) {
    Presentation presentation = event.getPresentation();

    MenuItem menuItem = menu.add(presentation.getTitle());

    if (presentation.getIcon() != -1) {
      menuItem.setIcon(presentation.getIcon());
    }
    menuItem.setTitle(presentation.getTitle());
    menuItem.setOnMenuItemClickListener(
        item -> {
          action.performAction();
          return true;
        });
  }

  public void fillDialogMenu(FragmentManager fragmentManager, ActionData data, String place) {
    OptionsSheetFragment options = OptionsSheetFragment.createSheet(data);

    for (Action action : actions.values()) {
      ActionEvent event = new ActionEvent(data, action.getPresentation(), place);
      action.update(event);

      if (event.getPresentation().isVisible()) {
        options.addAction(action);
      }
    }

    options.show(fragmentManager, "");
  }
}
