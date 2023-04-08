package com.raredev.vcspace.actions.main.filetoolbar;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.raredev.vcspace.activity.MainActivity;
import com.raredev.vcspace.fragments.ToolsFragment;
import com.vcspace.actions.ActionData;

public class OpenRecentAction extends MainBaseAction {

  @Override
  public void performAction(@NonNull ActionData data) {
    MainActivity main = getActivity(data);

    ((ToolsFragment) main.getSupportFragmentManager().findFragmentByTag("tools_fragment"))
        .fileTreeFragment.tryOpenRecentFolder();
    main.binding.drawerLayout.closeDrawer(GravityCompat.END);
    if (!main.binding.drawerLayout.isDrawerOpen(GravityCompat.START))
      main.binding.drawerLayout.openDrawer(GravityCompat.START);
  }

  @Override
  public int getTitle() {
    return R.string.open_recent;
  }

  @Override
  public int getIcon() {
    return R.drawable.history;
  }
}
