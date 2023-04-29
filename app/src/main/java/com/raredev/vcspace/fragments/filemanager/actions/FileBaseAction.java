package com.raredev.vcspace.fragments.filemanager.actions;

import android.content.Context;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import com.raredev.vcspace.adapters.FileListAdapter;
import com.raredev.vcspace.fragments.filemanager.FileManagerFragment;
import com.vcspace.actions.Action;
import com.vcspace.actions.ActionData;
import com.vcspace.actions.Presentation;
import com.vcspace.actions.location.DefaultLocations;
import java.io.File;

public abstract class FileBaseAction extends Action {

  @Override
  public void update(@NonNull ActionData data) {
    Presentation presentation = getPresentation();
    presentation.setVisible(false);

    FileManagerFragment fragment = getFragment(data);
    File file = getFile(data);
    if (fragment == null) {
      return;
    }
    if (file == null) {
      return;
    }

    presentation.setVisible(true);
    presentation.setEnabled(isApplicable(file, data));
    presentation.setTitle(getTitle(fragment.requireContext()));
    presentation.setIcon(getIcon());
  }

  @Override
  public String getLocation() {
    return DefaultLocations.FILE;
  }

  public abstract String getTitle(Context context);

  @DrawableRes
  public abstract int getIcon();

  public abstract boolean isApplicable(File file, ActionData data);

  public FileManagerFragment getFragment(ActionData data) {
    return data.get(FileManagerFragment.class);
  }
  
  public FileListAdapter getAdapter(ActionData data) {
    return data.get(FileListAdapter.class);
  }

  public File getFile(ActionData data) {
    return data.get(File.class);
  }
}
