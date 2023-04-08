package com.raredev.vcspace.actions.main.edit;

import androidx.annotation.NonNull;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.vcspace.actions.ActionData;

public class FindTextAction extends MainBaseAction {

  @Override
  public void performAction(@NonNull ActionData data) {
    var main = getActivity(data);
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
