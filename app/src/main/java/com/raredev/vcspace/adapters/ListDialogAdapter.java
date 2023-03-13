package com.raredev.vcspace.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.raredev.vcspace.databinding.LayoutListDialogBinding;
import com.raredev.vcspace.databinding.LayoutListItemBinding;
import com.raredev.vcspace.models.DialogListModel;
import java.util.List;

public class ListDialogAdapter extends RecyclerView.Adapter<ListDialogAdapter.VH> {
  private List<DialogListModel> listItems;
  private Listener listener;

  public ListDialogAdapter(List<DialogListModel> listItems) {
    this.listItems = listItems;
  }

  @Override
  public VH onCreateViewHolder(ViewGroup parent, int arg1) {
    return new VH(
        LayoutListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
  }

  @Override
  public void onBindViewHolder(VH holder, int position) {
    DialogListModel model = listItems.get(position);
    
    holder.name.setText(model.label);
    holder.icon.setImageResource(model.icon);
    holder.itemView.setOnClickListener(v -> listener.onClickListener(v, position));
  }

  @Override
  public int getItemCount() {
    return listItems.size();
  }

  public void setListener(Listener listener) {
    this.listener = listener;
  }

  public interface Listener {
    void onClickListener(View v, int position);
  }

  public class VH extends RecyclerView.ViewHolder {
    ImageView icon;
    TextView name;

    public VH(LayoutListItemBinding binding) {
      super(binding.getRoot());
      icon = binding.icon;
      name = binding.name;
    }
  }
}
