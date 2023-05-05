package com.raredev.vcspace.actions.main.other;

import android.content.Context;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import com.raredev.vcspace.R;
import com.raredev.vcspace.SimpleExecuter;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.vcspace.actions.ActionData;
import com.vcspace.actions.Presentation;

public class ExecuteAction extends MainBaseAction {

  @Override
  public void update(@NonNull ActionData data) {
    super.update(data);
    Presentation presentation = getPresentation();
    presentation.setVisible(false);

    var main = getActivity(data);
    if (main == null) {
      return;
    }
    if (main.getCurrentEditor() == null) {
      return;
    }

    presentation.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    presentation.setVisible(
        SimpleExecuter.isExecutable(main.getCurrentEditor().getDocument().toFile()));
  }

  @Override
  public void performAction(ActionData data) {
    var main = getActivity(data);
    main.saveAllFiles(false);
    new SimpleExecuter(main, main.getCurrentEditor().getDocument().toFile());
  }

  @Override
  public String getActionId() {
    return "execute.action";
  }

  @Override
  public String getTitle(Context context) {
    return context.getString(R.string.menu_execute);
  }

  @Override
  public int getIcon() {
    return R.drawable.ic_play;
  }
}
