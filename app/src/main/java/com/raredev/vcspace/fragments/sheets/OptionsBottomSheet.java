package com.raredev.vcspace.fragments.sheets;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.raredev.vcspace.adapters.OptionsAdapter;
import com.raredev.vcspace.databinding.LayoutSheetDialogBinding;
import com.raredev.vcspace.models.OptionModel;
import java.util.ArrayList;
import java.util.List;

public class OptionsBottomSheet extends BottomSheetDialogFragment {

  private List<OptionModel> options = new ArrayList<>();
  private OptionsAdapter.OptionListener listener;
  private OptionsAdapter adapter;

  private LayoutSheetDialogBinding binding;
  private Dialog dialog;

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = LayoutSheetDialogBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    adapter = new OptionsAdapter(options);
    if (listener != null) adapter.setOptionListener(listener);
    binding.list.setLayoutManager(new LinearLayoutManager(requireContext()));
    binding.list.setAdapter(adapter);
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    dialog = super.onCreateDialog(savedInstanceState);
    return dialog;
  }

  public void setOptionListener(OptionsAdapter.OptionListener listener) {
    this.listener = listener;
  }

  public void addOption(OptionModel option) {
    options.add(option);
  }
}
