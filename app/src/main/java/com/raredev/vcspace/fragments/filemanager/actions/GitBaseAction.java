package com.raredev.vcspace.fragments.filemanager.actions;

import android.content.Context;
import com.raredev.vcspace.fragments.filemanager.FileManagerFragment;
import com.vcspace.actions.Action;
import com.vcspace.actions.ActionData;
import com.vcspace.actions.Presentation;
import com.vcspace.actions.location.DefaultLocations;
import java.io.File;

public abstract class GitBaseAction extends Action {

  @Override
  public void update(ActionData data) {
    Presentation presentation = getPresentation();
    presentation.setVisible(false);

    FileManagerFragment fragment = getFragment(data);
    File file = getFolder(data);
    if (file == null) {
      return;
    }

    presentation.setVisible(true);
    presentation.setTitle(getTitle(fragment.requireActivity()));
    presentation.setIcon(getIcon());
  }

  @Override
  public String getLocation() {
    return DefaultLocations.GIT;
  }

  public FileManagerFragment getFragment(ActionData data) {
    return data.get(FileManagerFragment.class);
  }

  public File getFolder(ActionData data) {
    return data.get(File.class);
  }

  public abstract String getTitle(Context context);

  public int getIcon() {
    return -1;
  }
}
