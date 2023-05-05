package com.raredev.vcspace.actions.main.filetoolbar;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.vcspace.actions.ActionData;
import com.vcspace.actions.Presentation;

public class SaveAsAction extends MainBaseAction {

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

    presentation.setEnabled(true);
  }

  @Override
  public void performAction(@NonNull ActionData data) {
    var main = getActivity(data);

    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    intent.setType("text/*");
    intent.putExtra(Intent.EXTRA_TITLE, main.viewModel.getCurrentDocument().getName());

    main.launcher.launch(intent);
  }

  @Override
  public String getActionId() {
    return "save.as.action";
  }

  @Override
  public String getTitle(Context context) {
    return context.getString(R.string.menu_save_as);
  }

  @Override
  public int getIcon() {
    return R.drawable.ic_save;
  }
}
