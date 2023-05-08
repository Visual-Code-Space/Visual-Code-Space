package com.raredev.vcspace.actions.main.edit;

import android.content.Context;
import android.view.MenuItem;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.raredev.vcspace.ui.editor.CodeEditorView;
import com.vcspace.actions.ActionData;

public class ReloadEditorAction extends MainBaseAction {

  @Override
  public void performAction(ActionData data, MenuItem item) {
    var main = getActivity(data);

    CodeEditorView editor = main.getCurrentEditor();
    if (editor != null) {
      editor.reloadEditor();
    }
  }

  @Override
  public String getActionId() {
    return "reload.editor.action";
  }

  @Override
  public String getTitle(Context context) {
    return "Reload Editor" /*context.getString(R.string.menu_format)*/;
  }

  @Override
  public int getIcon() {
    return R.drawable.ic_refresh;
  }
}
