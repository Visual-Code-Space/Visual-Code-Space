package com.raredev.vcspace.actions.main.edit;

import com.blankj.utilcode.util.ToastUtils;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.vcspace.actions.ActionData;

public class FormatAction extends MainBaseAction {

  @Override
  public void performAction(ActionData data) {
    ToastUtils.showShort("Unable to perform action");
  }

  @Override
  public int getTitle() {
    return R.string.menu_format;
  }

  @Override
  public int getIcon() {
    return R.drawable.format_align_left;
  }
}
