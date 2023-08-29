package com.raredev.vcspace.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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

    holder.checkBox.setVisibility(selectedFiles.isEmpty() ? View.GONE : View.VISIBLE);
    holder.checkBox.setChecked(selectedFiles.contains(file));

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
          switchSelectedFile(file);
          return true;
        });

    holder.img_menu.setOnClickListener(
        v -> {
          if (fileListener != null) {
            fileListener.onFileMenuClick(selectedFiles, file, v);
          }
        });
  }

  @Override
  public int getItemCount() {
    if (files == null) {
      return 0;
    }
    return files.size();
  }

  public void switchSelectedFile(FileModel file) {
    if (selectedFiles.contains(file)) {
      selectedFiles.remove(file);
    } else {
      selectedFiles.add(file);
    }
    notifyDataSetChanged();
  }
  
  public void selectAllFiles() {
    for (FileModel file : files) {
      if (!selectedFiles.contains(file)) {
        selectedFiles.add(file);
      }
    }
    notifyDataSetChanged();
  }
  
  public void unselectAllFiles() {
    selectedFiles.clear();
    notifyDataSetChanged();
  }

  public void setFiles(List<FileModel> files) {
    this.files = files;
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

    void onFileMenuClick(List<FileModel> selectedFiles, FileModel file, View v);
  }

  public class VH extends RecyclerView.ViewHolder {
    CheckBox checkBox;
    ShapeableImageView img_icon, img_menu;
    MaterialTextView tv_name;

    public VH(LayoutFileItemBinding binding) {
      super(binding.getRoot());
      checkBox = binding.checkbox;
      img_icon = binding.imgIcon;
      tv_name = binding.fileName;
      img_menu = binding.imgMenu;
    }
  }
}
