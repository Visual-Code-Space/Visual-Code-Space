package com.raredev.vcspace.fragments.sheet;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.raredev.vcspace.adapters.OptionsSheetAdapter;
import com.raredev.vcspace.databinding.LayoutSheetDialogBinding;
import com.vcspace.actions.Action;
import com.vcspace.actions.ActionData;
import com.vcspace.actions.ActionManager;
import java.util.ArrayList;
import java.util.List;

public class ActionsSheetDialog extends BottomSheetDialogFragment {

  private LayoutSheetDialogBinding binding;

  private final List<Action> mOptions = new ArrayList<>();

  private OptionsSheetAdapter adapter;
  private OptionsSheetAdapter.Listener listener;

  private Dialog mDialog;

  public static ActionsSheetDialog createSheet(ActionData data, String location) {
    ActionsSheetDialog fragment = new ActionsSheetDialog();
    fragment.fillDialogMenu(data, location);
    return fragment;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    mDialog = super.onCreateDialog(savedInstanceState);
    mDialog.setOnShowListener((p1) -> onShow());
    return mDialog;
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = LayoutSheetDialogBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  public void onShow() {
    adapter = new OptionsSheetAdapter(mOptions);

    adapter.setListener(listener);

    binding.list.setLayoutManager(new LinearLayoutManager(getContext()));
    binding.list.setAdapter(adapter);
  }

  public void addAction(Action action) {
    if (!mOptions.contains(action)) {
      mOptions.add(action);
    }
  }

  public void fillDialogMenu(ActionData data, String location) {
    for (Action action : ActionManager.getInstance().getActions().values()) {
      action.update(data);

      if (action.visible && action.location == location) {
        addAction(action);
      }
    }
    listener =
        new OptionsSheetAdapter.Listener() {
          @Override
          public void onClickListener(Action action) {
            action.performAction(data);
            dismiss();
          }
        };
  }
}
