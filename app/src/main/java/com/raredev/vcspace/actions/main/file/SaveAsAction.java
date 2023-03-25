package com.raredev.vcspace.actions.main.file;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.ActionData;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.raredev.vcspace.activity.MainActivity;
import com.raredev.vcspace.ui.editor.manager.EditorManager;

public class SaveAsAction extends MainBaseAction {

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
    var main = (MainActivity) data.get(MainActivity.class);

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
