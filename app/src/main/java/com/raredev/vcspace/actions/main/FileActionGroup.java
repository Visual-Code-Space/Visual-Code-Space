package com.raredev.vcspace.actions.main;

import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.main.filetoolbar.*;
import com.raredev.vcspace.activity.MainActivity;
import com.vcspace.actions.Action;
import com.vcspace.actions.ActionData;
import com.vcspace.actions.ActionGroup;
import com.vcspace.actions.Presentation;
import com.vcspace.actions.location.DefaultLocations;

public class FileActionGroup extends ActionGroup {

  @Override
  public void update(@NonNull ActionData data) {
    Presentation presentation = getPresentation();
    presentation.setVisible(false);

    var main = (MainActivity) data.get(MainActivity.class);
    if (main == null) {
      return;
    }

    presentation.setVisible(true);
    presentation.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    presentation.setTitle(main.getString(R.string.file));
    presentation.setIcon(R.drawable.ic_folder);
  }

  @Override
  public String getActionId() {
    return "file.action.group";
  }

  @Override
  public String getLocation() {
    return DefaultLocations.MAIN_TOOLBAR;
  }

  @Override
  public Action[] getChildren(@Nullable ActionData data) {
    return new Action[] {
      new NewFileAction(),
      new OpenFileAction(),
      new SaveAction(),
      new SaveAsAction(),
      new SaveAllAction()
    };
  }
}
