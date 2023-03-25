package com.raredev.vcspace.actions.main.edit;

import androidx.annotation.NonNull;
import com.raredev.vcspace.actions.ActionData;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.raredev.vcspace.activity.MainActivity;
import com.raredev.vcspace.R;

public class FindTextAction extends MainBaseAction {

  @Override
  public void performAction(@NonNull ActionData data) {
    var main = (MainActivity) data.get(MainActivity.class);
    main.binding.searcher.showAndHide();
  }

  @Override
  public int getTitle() {
    return R.string.menu_search;
  }

  @Override
  public int getIcon() {
    return R.drawable.ic_search;
  }
}
