package com.raredev.vcspace.actions.main.other;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.vcspace.actions.ActionData;

public class OpenDrawerAction extends MainBaseAction {

  @Override
  public void performAction(@NonNull ActionData data) {
    var main = getActivity(data);

    main.binding.drawerLayout.openDrawer(GravityCompat.END);
  }

  @Override
  public int getTitle() {
    return R.string.menu_open_drawer;
  }

  @Override
  public int getIcon() {
    return -1;
  }
}
