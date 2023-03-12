package com.raredev.vcspace.ui.editor.symbol;

import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.raredev.vcspace.ui.editor.Symbol;
import com.raredev.vcspace.ui.editor.symbol.adapter.SymbolInputAdapter;
import com.raredev.vcspace.util.PreferencesUtils;
import io.github.rosemoe.sora.widget.CodeEditor;
import java.util.ArrayList;
import java.util.List;

public class SymbolInputView extends RecyclerView {
  private SymbolInputAdapter adapter;

  public SymbolInputView(Context context) {
    this(context, null);
  }

  public SymbolInputView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SymbolInputView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    adapter = new SymbolInputAdapter();
    setAdapter(adapter);

    setSymbols();
  }

  public void bindEditor(@NonNull CodeEditor editor) {
    adapter.bindEditor(editor);
  }

  public void setSymbols() {
    List<Symbol> symbols = new ArrayList<>();
    symbols.add(new Symbol("→", PreferencesUtils.useUseSpaces() ? "    " : "\t"));
    symbols.add(new Symbol("\""));
    symbols.add(new Symbol(";"));
    symbols.add(new Symbol("(", "()"));
    symbols.add(new Symbol(")"));
    symbols.add(new Symbol("{", "{}"));
    symbols.add(new Symbol("}"));
    symbols.add(new Symbol("[", "[]"));
    symbols.add(new Symbol("]"));
    symbols.add(new Symbol("<", "<>"));
    symbols.add(new Symbol(">"));

    adapter.setSymbols(symbols);
  }
}
