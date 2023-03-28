package com.raredev.vcspace.actions.main.other;

import androidx.annotation.NonNull;
import com.raredev.vcspace.R;
import com.raredev.vcspace.SimpleExecuter;
import com.raredev.vcspace.actions.ActionData;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.raredev.vcspace.activity.MainActivity;

public class ExecuteAction extends MainBaseAction {

  @Override
  public void update(@NonNull ActionData data) {
    super.update(data);
    visible = false;
    var main = (MainActivity) data.get(MainActivity.class);

    if (main == null) {
      return;
    }
    if (main.getCurrentEditor() == null) {
      return;
    }
    visible = SimpleExecuter.isExecutable(main.getCurrentEditor().getFile());
  }

  @Override
  public void performAction(ActionData data) {
    var main = (MainActivity) data.get(MainActivity.class);
    main.saveAllFiles(false);
    new SimpleExecuter(main, main.getCurrentEditor().getFile());
  }

  @Override
  public int getTitle() {
    return R.string.menu_execute;
  }

  @Override
  public int getIcon() {
    return R.drawable.ic_play;
  }
}
