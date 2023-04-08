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
import com.vcspace.actions.location.DefaultLocations;

public class FileActionGroup extends ActionGroup {

  public FileActionGroup() {
    this.location = DefaultLocations.MAIN_TOOLBAR;
  }

  @Override
  public void update(@NonNull ActionData data) {
    visible = true;

    var main = (MainActivity) data.get(MainActivity.class);
    if (main == null) {
      return;
    }

    title = main.getString(R.string.file);
    icon = R.drawable.ic_folder;
  }

  @Override
  public Action[] getChildren(@Nullable ActionData data) {
    return new Action[] {
      new NewFileAction(),
      new OpenFileAction(),
      new OpenFolderAction(),
      new OpenRecentAction(),
      new SaveAction(),
      new SaveAsAction(),
      new SaveAllAction()
    };
  }

  @Override
  public int getShowAsAction() {
    return MenuItem.SHOW_AS_ACTION_IF_ROOM;
  }
}
