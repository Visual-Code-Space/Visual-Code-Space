package com.raredev.vcspace.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.raredev.vcspace.databinding.LayoutFileItemBinding;
import com.raredev.vcspace.models.FileModel;
import java.util.LinkedList;
import java.util.List;

public class RecentFoldersAdapter extends RecyclerView.Adapter<RecentFoldersAdapter.VH> {

  private FolderListener listener;
  
  private List<FileModel> folders;

  public RecentFoldersAdapter() {
    folders = new LinkedList<>();
  }

  @NonNull
  @Override
  public VH onCreateViewHolder(ViewGroup parent, int arg1) {
    return new VH(
        LayoutFileItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
  }

  @Override
  public void onBindViewHolder(VH holder, int position) {
    FileModel folder = folders.get(position);
    holder.name.setText(folder.getName());
    holder.icon.setImageResource(folder.getIcon());

    holder.itemView.setOnClickListener(
        (v) -> {
          if (listener != null) {
            listener.onFolderClick(folder, v);
          }
        });

    holder.itemView.setOnLongClickListener(
        (v) -> {
          if (listener != null) {
            return listener.onFolderLongClick(folder, v);
          }
          return false;
        });
  }

  @Override
  public int getItemCount() {
    return folders == null ? 0 : folders.size();
  }

  public void setFiles(List<FileModel> data) {
    this.folders = data;
    notifyDataSetChanged();
  }

  public void clear() {
    if (folders == null) return;
    folders.clear();
    notifyDataSetChanged();
  }

  public void setFolderListener(FolderListener listener) {
    this.listener = listener;
  }

  public interface FolderListener {
    void onFolderClick(FileModel folder, View v);

    boolean onFolderLongClick(FileModel folder, View v);
  }

  public class VH extends RecyclerView.ViewHolder {
    ShapeableImageView icon;
    MaterialTextView name;

    public VH(LayoutFileItemBinding binding) {
      super(binding.getRoot());
      icon = binding.imgIcon;
      name = binding.fileName;
    }
  }
}
