package com.raredev.vcspace.actions.main.filetoolbar;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.raredev.vcspace.activity.MainActivity;
import com.vcspace.actions.ActionData;

public class SaveAsAction extends MainBaseAction {

  @Override
  public void update(@NonNull ActionData data) {
    super.update(data);
    enabled = false;
    var main = getActivity(data);

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
    var main = getActivity(data);

    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    intent.setType("text/*");
    intent.putExtra(Intent.EXTRA_TITLE, main.viewModel.getCurrentFile().getName());

    main.launcher.launch(intent);
  }

  @Override
  public int getTitle() {
    return R.string.menu_save_as;
  }

  @Override
  public int getIcon() {
    return R.drawable.ic_save;
  }
}
