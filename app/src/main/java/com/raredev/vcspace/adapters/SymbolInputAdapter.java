package com.raredev.vcspace.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.raredev.vcspace.databinding.LayoutSymbolItemBinding;
import com.raredev.vcspace.editor.IDECodeEditor;
import com.raredev.vcspace.models.Symbol;
import com.raredev.vcspace.utils.PreferencesUtils;
import java.util.List;

public class SymbolInputAdapter extends RecyclerView.Adapter<SymbolInputAdapter.VH> {

  private List<Symbol> symbolList;
  private IDECodeEditor editor;

  @NonNull
  @Override
  public VH onCreateViewHolder(ViewGroup parent, int arg1) {
    return new VH(
        LayoutSymbolItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
  }

  @Override
  public void onBindViewHolder(VH holder, int position) {
    Symbol symbol = symbolList.get(position);
    holder.itemView.setAnimation(
        AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.fade_in));

    holder.label.setText(symbol.getLabel());
    holder.label.setTypeface(
        ResourcesCompat.getFont(holder.itemView.getContext(), PreferencesUtils.getSelectedFont()));
    holder.itemView.setOnClickListener(v -> insertSymbol(symbol));
  }

  @Override
  public int getItemCount() {
    if (symbolList == null) {
      return 0;
    }
    return symbolList.size();
  }

  private void insertSymbol(Symbol symbol) {
    if (editor == null || !editor.isEditable()) return;
    final var controller = editor.getSnippetController();
    if ("→".equals(symbol.getLabel()) && controller.isInSnippet()) {
      controller.shiftToNextTabStop();
      return;
    }

    if ("→".equals(symbol.getLabel())) {
      editor.commitText(PreferencesUtils.getIndentationString());
      return;
    }

    String insertText = symbol.getInsert();
    if (insertText.length() == 2) {
      editor.insertText(insertText, 1);
    } else {
      editor.commitText(insertText, false);
    }
  }

  public void setSymbols(List<Symbol> symbols) {
    this.symbolList = symbols;
    notifyDataSetChanged();
  }

  public void bindEditor(@NonNull IDECodeEditor editor) {
    this.editor = editor;
  }

  public class VH extends RecyclerView.ViewHolder {
    MaterialButton label;

    public VH(LayoutSymbolItemBinding binding) {
      super(binding.getRoot());
      label = binding.label;
    }
  }
}
