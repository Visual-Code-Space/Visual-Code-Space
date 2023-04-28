package com.raredev.vcspace.actions.main;

import android.content.Context;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import com.raredev.vcspace.activity.MainActivity;
import com.vcspace.actions.Action;
import com.vcspace.actions.ActionData;
import com.vcspace.actions.Presentation;
import com.vcspace.actions.location.DefaultLocations;

public abstract class MainBaseAction extends Action {

  @Override
  public void update(@NonNull ActionData data) {
    Presentation presentation = getPresentation();

    var main = getActivity(data);
    if (main == null) {
      return;
    }
    presentation.setTitle(getTitle(main));
    presentation.setIcon(getIcon());
  }

  @Override
  public String getLocation() {
    return DefaultLocations.MAIN_TOOLBAR;
  }

  public abstract String getTitle(Context context);

  @DrawableRes
  public abstract int getIcon();

  public MainActivity getActivity(ActionData data) {
    return data.get(MainActivity.class);
  }
}
