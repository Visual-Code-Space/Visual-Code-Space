package com.raredev.vcspace.actions.main.edit;

import android.content.Context;
import android.view.MenuItem;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.raredev.vcspace.ui.editor.CodeEditorView;
import com.vcspace.actions.ActionData;

public class FormatAction extends MainBaseAction {

  @Override
  public void performAction(ActionData data, MenuItem item) {
    CodeEditorView editor = getActivity(data).getCurrentEditor();
    if (editor != null) {
      editor.getEditor().formatCodeAsync();
    }
  }

  @Override
  public String getActionId() {
    return "format.action";
  }

  @Override
  public String getTitle(Context context) {
    return context.getString(R.string.menu_format);
  }

  @Override
  public int getIcon() {
    return R.drawable.format_align_left;
  }
}
