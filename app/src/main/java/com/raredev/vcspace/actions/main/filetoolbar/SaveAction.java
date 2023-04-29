package com.raredev.vcspace.actions.main.filetoolbar;

import android.content.Context;
import androidx.annotation.NonNull;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.raredev.vcspace.activity.MainActivity;
import com.raredev.vcspace.util.ToastUtils;
import com.vcspace.actions.ActionData;
import com.vcspace.actions.Presentation;

public class SaveAction extends MainBaseAction {

  @Override
  public void update(@NonNull ActionData data) {
    super.update(data);
    Presentation presentation = getPresentation();
    presentation.setEnabled(false);

    var main = getActivity(data);
    if (main == null) {
      return;
    }
    if (main.getCurrentEditor() == null) {
      return;
    }

    presentation.setEnabled(main.getCurrentEditor().getEditor().isModified());
  }

  @Override
  public void performAction(@NonNull ActionData data) {
    MainActivity activity = getActivity(data);
    activity.saveFile();
  }

  @Override
  public String getActionId() {
    return "save.action";
  }

  @Override
  public String getTitle(Context context) {
    return context.getString(R.string.menu_save);
  }

  @Override
  public int getIcon() {
    return R.drawable.ic_save;
  }
}
