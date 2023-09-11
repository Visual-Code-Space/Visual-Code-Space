package com.raredev.vcspace.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.R;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.raredev.vcspace.databinding.LayoutFileItemBinding;
import com.raredev.vcspace.databinding.LayoutSheetItemBinding;
import com.raredev.vcspace.models.FileModel;
import com.raredev.vcspace.models.OptionModel;
import java.util.LinkedList;
import java.util.List;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.VH> {

  private OptionListener listener;
  private List<OptionModel> options;

  public OptionsAdapter(List<OptionModel> options) {
    this.options = options;
  }

  @NonNull
  @Override
  public VH onCreateViewHolder(ViewGroup parent, int arg1) {
    return new VH(
        LayoutSheetItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
  }

  @Override
  public void onBindViewHolder(VH holder, int position) {
    var option = options.get(position);

    holder.icon.setImageResource(option.getIcon());
    holder.name.setText(option.getName());

    holder.itemView.setOnClickListener(
        v -> {
          if (listener != null) listener.onOptionClick(option);
        });
  }

  @Override
  public int getItemCount() {
    return options == null ? 0 : options.size();
  }

  public void setOptionListener(OptionListener listener) {
    this.listener = listener;
  }

  public interface OptionListener {
    void onOptionClick(OptionModel option);
  }

  public class VH extends RecyclerView.ViewHolder {
    ImageView icon;
    TextView name;

    public VH(LayoutSheetItemBinding binding) {
      super(binding.getRoot());
      icon = binding.icon;
      name = binding.name;
    }
  }
}
