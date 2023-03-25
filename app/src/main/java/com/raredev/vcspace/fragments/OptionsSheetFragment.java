package com.raredev.vcspace.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.raredev.vcspace.actions.Action;
import com.raredev.vcspace.actions.ActionData;
import com.raredev.vcspace.adapters.OptionsSheetAdapter;
import com.raredev.vcspace.databinding.LayoutSheetDialogBinding;
import java.util.ArrayList;
import java.util.List;

public class OptionsSheetFragment extends BottomSheetDialogFragment {
  private final List<Action> mOptions = new ArrayList<>();
  private OptionsSheetAdapter adapter;
  private ActionData data;

  private LayoutSheetDialogBinding binding;
  private Dialog mDialog;

  public static OptionsSheetFragment createSheet(ActionData data) {
    OptionsSheetFragment fragment = new OptionsSheetFragment();
    fragment.setActionData(data);
    return fragment;
  }

  @Override
  @NonNull
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

    adapter.setListener(
        (action) -> {
          action.performAction(data);
          dismiss();
        });
    binding.list.setLayoutManager(new LinearLayoutManager(getContext()));
    binding.list.setAdapter(adapter);
  }

  public void setActionData(ActionData data) {
    this.data = data;
  }

  public void addAction(Action action) {
    if (!mOptions.contains(action)) {
      mOptions.add(action);
    }
  }

  public OptionsSheetFragment removeAction(int optionIndex) {
    return removeOption(mOptions.get(optionIndex));
  }

  public OptionsSheetFragment removeOption(Action action) {
    mOptions.remove(action);
    return this;
  }
}
