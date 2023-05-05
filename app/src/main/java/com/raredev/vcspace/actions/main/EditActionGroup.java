package com.raredev.vcspace.actions.main;

import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.main.edit.FindTextAction;
import com.raredev.vcspace.actions.main.edit.FormatAction;
import com.raredev.vcspace.actions.main.edit.ReloadEditorAction;
import com.raredev.vcspace.activity.MainActivity;
import com.vcspace.actions.Action;
import com.vcspace.actions.ActionData;
import com.vcspace.actions.ActionGroup;
import com.vcspace.actions.Presentation;
import com.vcspace.actions.location.DefaultLocations;

public class EditActionGroup extends ActionGroup {

  private Action[] subActions = {
    new FindTextAction(), new FormatAction(), new ReloadEditorAction()
  };

  @Override
  public void update(@NonNull ActionData data) {
    Presentation presentation = getPresentation();
    presentation.setVisible(false);

    var main = (MainActivity) data.get(MainActivity.class);
    if (main == null) {
      return;
    }
    if (main.getCurrentEditor() == null) {
      return;
    }

    presentation.setVisible(true);
    presentation.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    presentation.setTitle(main.getString(R.string.menu_edit));
    presentation.setIcon(R.drawable.pencil);
  }

  @Override
  public String getActionId() {
    return "edit.action.group";
  }

  @Override
  public String getLocation() {
    return DefaultLocations.MAIN_TOOLBAR;
  }

  @Override
  public Action[] getChildren(@Nullable ActionData data) {
    return subActions;
  }
}
