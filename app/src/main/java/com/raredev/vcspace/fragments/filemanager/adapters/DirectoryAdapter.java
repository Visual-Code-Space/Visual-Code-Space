package com.raredev.vcspace.fragments.filemanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.imageview.ShapeableImageView;
import com.raredev.vcspace.databinding.LayoutDirectoryItemBinding;
import com.raredev.vcspace.fragments.filemanager.models.DirectoryModel;
import com.raredev.vcspace.fragments.filemanager.viewmodel.FileListViewModel;

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.VH> {

  private FileListViewModel viewModel;

  public DirectoryAdapter(FileListViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @NonNull
  @Override
  public VH onCreateViewHolder(ViewGroup parent, int arg1) {
    return new VH(
        LayoutDirectoryItemBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false));
  }

  @Override
  public void onBindViewHolder(VH holder, int position) {
    DirectoryModel directory = viewModel.getDirectories().get(position);

    holder.directory.setText(directory.getName());

    Context context = holder.directory.getContext();

    if (directory.equals(viewModel.getCurrentDir())) {
      holder.directory.setTextColor(MaterialColors.getColor(context, R.attr.colorPrimary, 0));
      holder.separator.setVisibility(View.GONE);
    } else {
      holder.directory.setTextColor(MaterialColors.getColor(context, R.attr.colorControlNormal, 0));
      holder.separator.setVisibility(View.VISIBLE);
    }

    holder.directory.setOnClickListener(
        (v) -> {
          if (viewModel.getCurrentDir() == directory) {
            return;
          }
          viewModel.setCurrentDir(directory);
          viewModel.removeAllDirectoriesAfter(position);
          notifyDataSetChanged();
        });
  }

  @Override
  public int getItemCount() {
    return viewModel.getDirectories().size();
  }

  public class VH extends RecyclerView.ViewHolder {
    MaterialButton directory;
    ShapeableImageView separator;

    public VH(LayoutDirectoryItemBinding binding) {
      super(binding.getRoot());
      directory = binding.directory;
      separator = binding.separator;
    }
  }
}
