package com.raredev.vcspace.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.R;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.raredev.vcspace.databinding.LayoutFileItemBinding;
import com.raredev.vcspace.models.FileModel;
import java.util.LinkedList;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.VH> {

  private FileListener fileListener;

  private List<FileModel> selectedFiles;
  private List<FileModel> files;

  public FileAdapter() {
    selectedFiles = new LinkedList<>();
    files = new LinkedList<>();
  }

  @NonNull
  @Override
  public VH onCreateViewHolder(ViewGroup parent, int arg1) {
    return new VH(
        LayoutFileItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
  }

  @Override
  public void onBindViewHolder(VH holder, int position) {
    FileModel file = files.get(position);
    holder.tv_name.setText(file.getName());
    holder.img_icon.setImageResource(file.getIcon());

    int colorControlHighlight =
        MaterialColors.getColor(holder.itemView.getContext(), R.attr.colorControlHighlight, 0);
    holder.itemView.setBackgroundColor(
        selectedFiles.contains(file) ? colorControlHighlight : Color.TRANSPARENT);

    holder.itemView.setOnClickListener(
        (v) -> {
          if (!selectedFiles.isEmpty()) {
            switchSelectedFile(file);
            return;
          }
          if (fileListener != null) {
            fileListener.onFileClick(file, v);
          }
        });

    holder.itemView.setOnLongClickListener(
        (v) -> {
          if (fileListener != null) {
            return fileListener.onFileLongClick(selectedFiles, file, v);
          }
          return false;
        });

    holder.img_icon.setOnClickListener(v -> switchSelectedFile(file));
  }

  @Override
  public int getItemCount() {
    return files == null ? 0 : files.size();
  }

  public void switchSelectedFile(FileModel file) {
    if (selectedFiles.contains(file)) {
      selectedFiles.remove(file);
    } else {
      selectedFiles.add(file);
    }
    notifyItemChanged(files.indexOf(file));
  }

  public void selectAllFiles() {
    for (FileModel file : files) {
      if (!selectedFiles.contains(file)) {
        selectedFiles.add(file);
        notifyItemChanged(files.indexOf(file));
      }
    }
  }

  public void unselectAllFiles() {
    for (FileModel file : files) {
      if (selectedFiles.contains(file)) {
        selectedFiles.remove(file);
        notifyItemChanged(files.indexOf(file));
      }
    }
  }

  public void setFiles(List<FileModel> data) {
    this.files = data;
    selectedFiles.clear();
    notifyDataSetChanged();
  }

  public void clear() {
    if (files == null) return;
    selectedFiles.clear();
    files.clear();
    notifyDataSetChanged();
  }

  public void setFileListener(FileListener listener) {
    this.fileListener = listener;
  }

  public interface FileListener {
    void onFileClick(FileModel file, View v);

    boolean onFileLongClick(List<FileModel> selectedFiles, FileModel file, View v);
  }

  public class VH extends RecyclerView.ViewHolder {
    ShapeableImageView img_icon;
    MaterialTextView tv_name;

    public VH(LayoutFileItemBinding binding) {
      super(binding.getRoot());
      img_icon = binding.imgIcon;
      tv_name = binding.fileName;
    }
  }
}
