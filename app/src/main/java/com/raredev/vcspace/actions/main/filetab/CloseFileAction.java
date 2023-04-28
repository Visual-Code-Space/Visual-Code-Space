package com.raredev.vcspace.actions.main.filetab;

import android.content.Context;
import androidx.annotation.NonNull;
import com.raredev.vcspace.R;
import com.raredev.vcspace.actions.main.FileTabBaseAction;
import com.vcspace.actions.ActionData;

public class CloseFileAction extends FileTabBaseAction {

  @Override
  public void performAction(@NonNull ActionData data) {
    var main = getActivity(data);
    main.closeFile(main.viewModel.getCurrentFileIndex());
  }

  @Override
  public String getActionId() {
    return "close.file.action";
  }

  @Override
  public String getTitle(Context context) {
    return context.getString(R.string.close);
  }
}
