package com.raredev.vcspace.actions.main.edit;

import android.content.Context;
import androidx.annotation.NonNull;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.main.MainBaseAction;
import com.raredev.vcspace.ui.editor.CodeEditorView;
import com.vcspace.actions.ActionData;

public class FindTextAction extends MainBaseAction {

  @Override
  public void performAction(@NonNull ActionData data) {
    var main = getActivity(data);
    
    CodeEditorView editor = main.getCurrentEditor();
    if (editor != null) {
      editor.showAndHideSearcher();
    }
  }

  @Override
  public String getActionId() {
    return "find.text.action";
  }

  @Override
  public String getTitle(Context context) {
    return context.getString(R.string.menu_search);
  }

  @Override
  public int getIcon() {
    return R.drawable.ic_search;
  }
}
