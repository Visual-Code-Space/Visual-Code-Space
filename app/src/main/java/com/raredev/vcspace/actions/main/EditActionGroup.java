package com.raredev.vcspace.actions.main;

import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.main.edit.FindTextAction;
import com.raredev.vcspace.actions.main.edit.FormatAction;
import com.raredev.vcspace.activity.MainActivity;
import com.vcspace.actions.Action;
import com.vcspace.actions.ActionData;
import com.vcspace.actions.ActionGroup;
import com.vcspace.actions.location.DefaultLocations;

public class EditActionGroup extends ActionGroup {

  public EditActionGroup() {
    location = DefaultLocations.MAIN_TOOLBAR;
  }

  @Override
  public void update(@NonNull ActionData data) {
    visible = false;
    var main = (MainActivity) data.get(MainActivity.class);

    if (main == null) {
      return;
    }
    if (main.getCurrentEditor() == null) {
      return;
    }
    visible = true;
    title = ((MainActivity) data.get(MainActivity.class)).getString(R.string.menu_edit);
    icon = R.drawable.pencil;
  }

  @Override
  public Action[] getChildren(@Nullable ActionData data) {
    return new Action[] {new FindTextAction(), new FormatAction()};
  }

  @Override
  public int getShowAsAction() {
    return MenuItem.SHOW_AS_ACTION_IF_ROOM;
  }
}
