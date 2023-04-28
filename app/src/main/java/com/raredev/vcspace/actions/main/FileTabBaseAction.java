package com.raredev.vcspace.actions.main;

import android.content.Context;
import androidx.annotation.NonNull;
import com.raredev.vcspace.activity.MainActivity;
import com.vcspace.actions.Action;
import com.vcspace.actions.ActionData;
import com.vcspace.actions.Presentation;
import com.vcspace.actions.location.DefaultLocations;

public abstract class FileTabBaseAction extends Action {

  @Override
  public void update(@NonNull ActionData data) {
    Presentation presentation = getPresentation();

    var main = getActivity(data);
    if (main == null) {
      return;
    }

    presentation.setTitle(getTitle(main));
  }

  @Override
  public String getLocation() {
    return DefaultLocations.FILE_TAB;
  }

  public abstract String getTitle(Context context);

  public MainActivity getActivity(ActionData data) {
    return data.get(MainActivity.class);
  }
}
