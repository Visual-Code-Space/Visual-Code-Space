package com.raredev.vcspace.actions.main.file;

import androidx.annotation.NonNull;
import com.blankj.utilcode.util.ToastUtils;
import com.raredev.vcspace.actions.ActionData;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.raredev.vcspace.R;
import com.raredev.vcspace.activity.MainActivity;
import com.raredev.vcspace.ui.editor.manager.EditorManager;

public class SaveAction extends MainBaseAction {

  @Override
  public void update(@NonNull ActionData data) {
    super.update(data);
    enabled = false;
    var editorManager = (EditorManager) data.get(EditorManager.class);

    if (editorManager == null) {
      return;
    }
    if (editorManager.getCurrentEditor() == null) {
      return;
    }
    enabled = editorManager.getCurrentEditor() != null;
  }

  @Override
  public void performAction(@NonNull ActionData data) {
    MainActivity main = (MainActivity) data.get(MainActivity.class);

    main.editorManager.getCurrentEditor().save();
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
