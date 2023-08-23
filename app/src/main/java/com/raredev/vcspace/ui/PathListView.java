package com.raredev.vcspace.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.raredev.vcspace.adapters.PathListAdapter;
import com.raredev.vcspace.ui.panels.file.FileExplorerPanel;
import java.io.File;

public class PathListView extends RecyclerView {

  public static final int TYPE_FILE_PATH = 0;
  public static final int TYPE_FOLDER_PATH = 1;

  private PathListAdapter adapter;
  private boolean enabled;

  public PathListView(Context context) {
    this(context, null);
  }

  public PathListView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public PathListView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
    setAdapter(adapter = new PathListAdapter());

    enabled = true;
  }

  public void setEnabled(boolean enabled) {
    setVisibility(enabled ? View.VISIBLE : View.GONE);
    this.enabled = enabled;
  }

  public void setType(int type) {
    adapter.setType(type);
  }

  public void setFileExplorerPanel(FileExplorerPanel viewModel) {
    adapter.setFileExplorerPanel(viewModel);
  }

  public void setPath(String path) {
    if (enabled) {
      if (path == null) {
        adapter.setPath(null);
        return;
      }
      adapter.setPath(new File(path));

      scrollToPosition(adapter.getItemCount() - 1);
    }
  }
}
