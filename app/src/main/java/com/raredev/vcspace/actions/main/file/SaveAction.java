package com.raredev.vcspace.actions.main.file;

import androidx.annotation.NonNull;
import com.blankj.utilcode.util.ToastUtils;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.ActionData;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.raredev.vcspace.activity.MainActivity;

public class SaveAction extends MainBaseAction {

  @Override
  public void update(@NonNull ActionData data) {
    super.update(data);
    enabled = false;
    var main = (MainActivity) data.get(MainActivity.class);

    if (main == null) {
      return;
    }
    if (main.getCurrentEditor() == null) {
      return;
    }
    enabled = main.getCurrentEditor() != null;
  }

  @Override
  public void performAction(@NonNull ActionData data) {
    ((MainActivity) data.get(MainActivity.class)).getCurrentEditor().save();
    ToastUtils.showShort(R.string.saved);
  }

  @Override
  public int getTitle() {
    return R.string.menu_save;
  }

  @Override
  public int getIcon() {
    return R.drawable.ic_save;
  }
}
