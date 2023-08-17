package com.raredev.vcspace.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.R;
import com.google.android.material.color.MaterialColors;
import com.raredev.vcspace.databinding.LayoutPathItemBinding;
import com.raredev.vcspace.models.FileModel;
import com.raredev.vcspace.viewmodel.FileListViewModel;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PathListView extends RecyclerView {

  public static final int TYPE_FILE_PATH = 0;
  public static final int TYPE_FOLDER_PATH = 1;

  private PathListAdapter adapter;
  private FileListViewModel viewModel;

  private boolean enabled;

  private int type;

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
    this.type = type;
  }

  public void setFileViewModel(FileListViewModel viewModel) {
    this.viewModel = viewModel;
  }

  public void setPath(String path) {
    if (enabled) {
      adapter.setPath(new File(path));
    }
  }

  public class PathListAdapter extends RecyclerView.Adapter<VH> {

    private List<FileModel> paths = new ArrayList<>();

    public PathListAdapter() {}

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
      return new VH(LayoutPathItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
      FileModel path = paths.get(position);

      holder.path.setText(path.getName());
      if (position == getItemCount() - 1) {
        holder.path.setTextColor(MaterialColors.getColor(getContext(), R.attr.colorPrimary, 0));
        holder.separator.setVisibility(View.GONE);
      } else {
        holder.path.setTextColor(
              MaterialColors.getColor(getContext(), R.attr.colorControlNormal, 0));
        holder.separator.setVisibility(View.VISIBLE);
      }

      holder.itemView.setOnClickListener(
          (v) -> {
            if (type == TYPE_FILE_PATH) {
              if (!path.isFile()) new FileTreePopupWindow(getContext(), v).setPath(path.getPath());
            } else {
              if (position == getItemCount() - 1) {
                return;
              }
              viewModel.setCurrentDir(path);
            }
          });
    }

    @Override
    public int getItemCount() {
      return paths.size();
    }

    public void setPath(File path) {
      paths.clear();

      for (; path != null; ) {
        if (path.getPath().equals("/storage/emulated")) {
          break;
        }
        var fileModel = FileModel.fileToFileModel(path);
        if (fileModel.getPath().equals("/storage/emulated/0")) {
          fileModel.setName("sdcard");
        }
        paths.add(fileModel);
        path = path.getParentFile();
      }

      Collections.reverse(paths);
      notifyDataSetChanged();
      
      scrollToPosition(adapter.getItemCount() - 1);
    }
  }

  public class VH extends RecyclerView.ViewHolder {
    TextView path;
    ImageView separator;
    
    LayoutPathItemBinding bind;

    public VH(LayoutPathItemBinding binding) {
      super(binding.getRoot());
      bind = binding;
      path = binding.path;
      separator = binding.separator;
    }
  }
}
