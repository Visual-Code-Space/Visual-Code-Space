package com.raredev.vcspace.actions.main;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import com.raredev.vcspace.activity.MainActivity;
import com.vcspace.actions.Action;
import com.vcspace.actions.ActionData;
import com.vcspace.actions.location.DefaultLocations;

public abstract class FileTabBaseAction extends Action {

  public FileTabBaseAction() {
    location = DefaultLocations.FILE_TAB;
  }

  @Override
  public void update(@NonNull ActionData data) {
    visible = true;

    var main = getActivity(data);
    if (main == null) {
      return;
    }

    title = main.getString(getTitle());
  }

  @StringRes
  public abstract int getTitle();

  public MainActivity getActivity(ActionData data) {
    return (MainActivity) data.get(MainActivity.class);
  }
}
