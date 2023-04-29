package com.raredev.vcspace.fragments.filemanager.actions.file;

import android.content.Context;
import androidx.annotation.NonNull;
import com.blankj.utilcode.util.ClipboardUtils;
import com.raredev.vcspace.R;
import com.raredev.vcspace.adapters.FileListAdapter;
import com.raredev.vcspace.fragments.filemanager.actions.FileBaseAction;
import com.vcspace.actions.ActionData;
import java.io.File;

public class CopyPathAction extends FileBaseAction {

  @Override
  public boolean isApplicable(File file, ActionData data) {
    FileListAdapter adapter = getAdapter(data);
    return !adapter.isFilesSelected();
  }

  @Override
  public void performAction(@NonNull ActionData data) {
    File file = getFile(data);
    String path = file.getAbsolutePath();
    
    ClipboardUtils.copyText(path);
  }
  
  @Override
  public String getActionId() {
    return "copy.path.action";
  }
  
  @Override
  public String getTitle(Context context) {
    return context.getString(R.string.copy_path);
  }

  @Override
  public int getIcon() {
    return R.drawable.content_copy;
  }
}
