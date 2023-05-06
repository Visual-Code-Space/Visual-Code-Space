package com.vcspace.actions;

import android.view.Menu;
import androidx.annotation.NonNull;
import com.vcspace.actions.impl.ActionManagerImpl;
import java.util.Map;

public abstract class ActionManager {

  private static ActionManager actionManager;

  public static ActionManager getInstance() {
    if (actionManager == null) {
      actionManager = new ActionManagerImpl();
    }
    return actionManager;
  }

  public abstract void fillMenu(Menu menu, ActionData data, String location);

  public abstract void registerAction(@NonNull String id, @NonNull Action action);

  public abstract void registerAction(@NonNull Action action);

  public abstract void performAction(@NonNull String id, @NonNull ActionData data);

  public abstract void unregisterAction(@NonNull Action action);

  public abstract void unregisterAction(@NonNull String actionId);

  public abstract Map<String, Action> getActions();

  public abstract void clear();
}
