package com.raredev.vcspace.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.raredev.vcspace.adapters.PathListAdapter;
import com.raredev.vcspace.ui.panels.file.FileExplorerPanel;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;
import java.io.File;

public class PathListView extends RecyclerView {

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

  public void setFileExplorerPanel(FileExplorerPanel viewModel) {
    adapter.setFileExplorerPanel(viewModel);
  }

  public void setColorScheme(EditorColorScheme colorScheme) {
    setBackgroundColor(colorScheme.getColor(EditorColorScheme.WHOLE_BACKGROUND));
    adapter.setColorScheme(colorScheme);
  }

  public void setPath(String path) {
    if (!enabled || path == null) {
      adapter.setPath(null);
      return;
    }
    if (path.startsWith("/data")) {
      adapter.setPath(null);
      return;
    }

    adapter.setPath(new File(path));

    scrollToPosition(adapter.getItemCount() - 1);
  }
}
