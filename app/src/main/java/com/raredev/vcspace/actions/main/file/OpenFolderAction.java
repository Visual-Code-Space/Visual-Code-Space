package com.raredev.vcspace.actions.main.file;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.ActionData;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.raredev.vcspace.activity.MainActivity;
import com.raredev.vcspace.fragments.ToolsFragment;

public class OpenFolderAction extends MainBaseAction {

  @Override
  public void performAction(@NonNull ActionData data) {
    MainActivity main = (MainActivity) data.get(MainActivity.class);

    ((ToolsFragment) main.getSupportFragmentManager().findFragmentByTag("tools_fragment"))
        .mStartForResult.launch(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE));
    main.binding.drawerLayout.closeDrawer(GravityCompat.END);
  }

  @Override
  public int getTitle() {
    return R.string.open_folder;
  }

  @Override
  public int getIcon() {
    return R.drawable.folder_open;
  }
}
