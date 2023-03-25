package com.raredev.vcspace.actions.main;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.ActionData;
import com.raredev.vcspace.activity.MainActivity;

public class OpenDrawerAction extends MainBaseAction {

  @Override
  public void performAction(@NonNull ActionData data) {
    var main = (MainActivity) data.get(MainActivity.class);

    main.binding.drawerLayout.openDrawer(GravityCompat.END);
  }

  @Override
  public int getTitle() {
    return R.string.app_name;
  }

  @Override
  public int getIcon() {
    return R.drawable.dots_vertical;
  }
}
