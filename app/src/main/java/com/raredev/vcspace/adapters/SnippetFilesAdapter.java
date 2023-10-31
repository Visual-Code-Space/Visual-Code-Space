package com.raredev.vcspace.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.raredev.vcspace.databinding.LayoutFileItemBinding;
import com.raredev.vcspace.models.UserSnippetModel;
import com.raredev.vcspace.res.R;
import java.io.File;
import java.util.List;

public class SnippetFilesAdapter extends RecyclerView.Adapter<SnippetFilesAdapter.VH> {

  private SnippetFileListener listener;
  private List<UserSnippetModel> userSnippets;

  @NonNull
  @Override
  public VH onCreateViewHolder(ViewGroup parent, int arg1) {
    return new VH(
        LayoutFileItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
  }

  @Override
  public void onBindViewHolder(VH holder, int position) {
    holder.itemView.setAnimation(
        AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.fade_in));

    UserSnippetModel userSnippet = userSnippets.get(position);
    File snippetFile = userSnippet.getSnippetFile();

    String name = snippetFile.getName().replace(".json", "");
    if (snippetFile.exists()) {
      name = snippetFile.getName();
    }

    holder.tv_name.setText(name + " (" + userSnippet.getLanguageName() + ")");

    holder.img_icon.setImageResource(R.drawable.ic_file);

    holder.itemView.setOnClickListener(
        (v) -> {
          if (listener != null) {
            listener.onFileClick(userSnippet, v);
          }
        });

    holder.itemView.setOnLongClickListener(
        (v) -> {
          if (listener != null) {
            listener.onFileLongClick(userSnippet, v);
          }
          return true;
        });
  }

  @Override
  public int getItemCount() {
    if (userSnippets == null) {
      return 0;
    }
    return userSnippets.size();
  }

  public void setData(List<UserSnippetModel> userSnippets) {
    this.userSnippets = userSnippets;
    notifyDataSetChanged();
  }

  public void clear() {
    if (userSnippets == null) return;
    userSnippets.clear();
    notifyDataSetChanged();
  }
  
  public UserSnippetModel getUserSnippet(int position) {
    if (userSnippets == null) return null;
    return userSnippets.get(position);
  }

  public void setSnippetFileListener(SnippetFileListener listener) {
    this.listener = listener;
  }

  public interface SnippetFileListener {
    void onFileClick(UserSnippetModel file, View v);

    void onFileLongClick(UserSnippetModel file, View v);
  }

  public class VH extends RecyclerView.ViewHolder {
    ImageView img_icon;
    TextView tv_name;

    public VH(LayoutFileItemBinding binding) {
      super(binding.getRoot());
      binding.getRoot().setPadding(5, 5, 5, 5);
      img_icon = binding.icon;
      tv_name = binding.name;
    }
  }
}
