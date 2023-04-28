package com.raredev.vcspace.fragments.filemanager.actions;

import com.vcspace.actions.ActionData;
import com.vcspace.actions.location.DefaultLocations;
import java.io.File;

public abstract class TopbarBaseAction extends FileBaseAction {

  @Override
  public String getLocation() {
    return DefaultLocations.FILE_TOPBAR;
  }
  
  @Override
  public boolean isApplicable(File file, ActionData data) {
    return true;
  }
}
