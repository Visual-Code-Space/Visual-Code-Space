package com.raredev.vcspace.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.raredev.vcspace.actions.Action;
import com.raredev.vcspace.databinding.LayoutListItemBinding;
import java.util.List;

public class OptionsSheetAdapter extends RecyclerView.Adapter<OptionsSheetAdapter.VH> {
  private List<Action> listItems;
  private Listener listener;

  public OptionsSheetAdapter(List<Action> listItems) {
    this.listItems = listItems;
  }

  @Override
  public VH onCreateViewHolder(ViewGroup parent, int arg1) {
    return new VH(
        LayoutListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
  }

  @Override
  public void onBindViewHolder(VH holder, int position) {
    Action action = listItems.get(position);

    holder.name.setText(action.title);
    holder.icon.setImageResource(action.icon);
    holder.itemView.setOnClickListener(v -> listener.onClickListener(action));
  }

  @Override
  public int getItemCount() {
    return listItems.size();
  }

  public void setListener(Listener listener) {
    this.listener = listener;
  }

  public interface Listener {
    void onClickListener(Action action);
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
