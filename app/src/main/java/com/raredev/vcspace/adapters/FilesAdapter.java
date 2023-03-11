package com.raredev.vcspace.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.divider.MaterialDivider;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.raredev.vcspace.databinding.LayoutFileItemBinding;
import com.raredev.vcspace.R;
import com.raredev.vcspace.tools.FileExtension;
import com.raredev.vcspace.util.FileManagerUtils;
import java.io.File;
import java.util.List;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.VH> {
  private FileListener fileListener;
  private List<File> filesList;

  public FilesAdapter(List<File> itemList) {
    this.filesList = itemList;
  }

  @NonNull
  @Override
  public VH onCreateViewHolder(ViewGroup parent, int arg1) {
    return new VH(
        LayoutFileItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
  }

  @Override
  public void onBindViewHolder(VH holder, int position) {
    File file = filesList.get(position);
    holder.itemView.setAnimation(
        AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.fade_in));

    holder.tv_name.setText(file.getName());

    if (file.isFile() && position > 0) {
      holder.img_icon.setImageResource(FileExtension.getIcon(file.getName()).icon);
    } else {
      holder.img_icon.setImageResource(R.drawable.ic_folder);
    }

    if (position == getItemCount() - 1) {
      holder.divider.setVisibility(View.GONE);
    } else {
      holder.divider.setVisibility(View.VISIBLE);
    }

    holder.itemView.setOnClickListener(
        (v) -> {
          if (fileListener != null) fileListener.onFileClick(position, v);
        });

    holder.itemView.setOnLongClickListener(
        (v) -> {
          if (fileListener != null) {
            return fileListener.onFileLongClick(position, v);
          }
          return false;
        });
  }

  @Override
  public int getItemCount() {
    return filesList.size();
  }

  public void refresh(List<File> list) {
    this.filesList = list;
    notifyDataSetChanged();
  }

  public void setFileListener(FileListener listener) {
    this.fileListener = listener;
  }

  public interface FileListener {
    void onFileClick(int arg01, View arg02);

    boolean onFileLongClick(int arg01, View arg02);
  }

  public class VH extends RecyclerView.ViewHolder {
    ShapeableImageView img_icon;
    MaterialTextView tv_name;

    MaterialDivider divider;

    public VH(LayoutFileItemBinding binding) {
      super(binding.getRoot());
      divider = binding.divider;
      img_icon = binding.icon;
      tv_name = binding.name;
    }
  }
}
