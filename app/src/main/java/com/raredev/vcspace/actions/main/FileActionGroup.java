package com.raredev.vcspace.actions.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.Action;
import com.raredev.vcspace.actions.ActionData;
import com.raredev.vcspace.actions.ActionGroup;
import com.raredev.vcspace.actions.main.file.NewFileAction;
import com.raredev.vcspace.actions.main.file.OpenFileAction;
import com.raredev.vcspace.actions.main.file.OpenFolderAction;
import com.raredev.vcspace.actions.main.file.OpenRecentAction;
import com.raredev.vcspace.actions.main.file.SaveAction;
import com.raredev.vcspace.actions.main.file.SaveAllAction;
import com.raredev.vcspace.actions.main.file.SaveAsAction;

public class FileActionGroup extends ActionGroup {

  public FileActionGroup() {
    location = Action.Location.MAIN_TOOLBAR;
  }

  @Override
  public void update(@NonNull ActionData data) {
    visible = true;
    title = R.string.file;
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
}
