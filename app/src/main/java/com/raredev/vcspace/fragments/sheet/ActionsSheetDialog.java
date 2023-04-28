package com.raredev.vcspace.fragments.sheet;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.raredev.vcspace.databinding.LayoutListItemBinding;
import com.raredev.vcspace.databinding.LayoutSheetDialogBinding;
import com.vcspace.actions.Action;
import com.vcspace.actions.ActionData;
import com.vcspace.actions.ActionManager;
import com.vcspace.actions.Presentation;
import java.util.ArrayList;
import java.util.List;

public class ActionsSheetDialog extends BottomSheetDialogFragment {

  private LayoutSheetDialogBinding binding;

  private List<Action> actions = new ArrayList<>();
  private ActionsSheetAdapter adapter;

  private OnActionClickListener listener;

  private Dialog mDialog;

  public static ActionsSheetDialog createSheet(ActionData data, String location) {
    ActionsSheetDialog fragment = new ActionsSheetDialog();
    fragment.fillDialogMenu(data, location);
    return fragment;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    mDialog = super.onCreateDialog(savedInstanceState);
    mDialog.setOnShowListener(
        (p1) -> {
          adapter = new ActionsSheetAdapter(actions);

          adapter.setListener(listener);

          binding.list.setLayoutManager(new LinearLayoutManager(getContext()));
          binding.list.setAdapter(adapter);
        });
    return mDialog;
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = LayoutSheetDialogBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  public void fillDialogMenu(ActionData data, String location) {
    for (Action action : ActionManager.getInstance().getActions().values()) {
      if (action.getLocation().equals(location)) {
        action.update(data);

        Presentation presentation = action.getPresentation();
        if (presentation.isVisible()) {
          actions.add(action);
        }
      }
    }
    listener =
        new OnActionClickListener() {
          @Override
          public void onClickListener(Action action) {
            action.performAction(data);
            dismiss();
          }
        };
  }

  class ActionsSheetAdapter extends RecyclerView.Adapter<ActionsSheetAdapter.VH> {

    private OnActionClickListener listener;
    private List<Action> actions;

    public ActionsSheetAdapter(List<Action> actions) {
      this.actions = actions;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
      return new VH(
          LayoutListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
      Action action = actions.get(position);

      Presentation presentation = action.getPresentation();

      holder.name.setText(presentation.getTitle());
      holder.icon.setImageResource(presentation.getIcon());
      holder.itemView.setOnClickListener(
          v -> {
            if (listener != null) {
              listener.onClickListener(action);
            }
          });
    }

    @Override
    public int getItemCount() {
      return actions.size();
    }

    public void setListener(OnActionClickListener listener) {
      this.listener = listener;
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

  interface OnActionClickListener {
    void onClickListener(Action action);
  }
}
