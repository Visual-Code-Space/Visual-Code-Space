package com.raredev.vcspace.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import com.raredev.vcspace.databinding.LayoutSearcherBinding;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.EditorSearcher;
import io.github.rosemoe.sora.widget.VCSpaceSearcher;

public class VCSpaceSearcherLayout extends LinearLayout implements View.OnClickListener {
  private LayoutSearcherBinding binding;

  private EditorSearcher.SearchOptions searchOptions =
      new EditorSearcher.SearchOptions(true, false);
  private VCSpaceSearcher searcher;
  private CodeEditor editor;

  public boolean isShowing = false;

  public VCSpaceSearcherLayout(Context context, IDECodeEditor editor) {
    super(context);
    this.editor = editor;
    searcher = (VCSpaceSearcher) editor.getSearcher();

    binding = LayoutSearcherBinding.inflate(LayoutInflater.from(getContext()));
    binding.getRoot().setVisibility(View.GONE);
    removeAllViews();
    addView(
        binding.getRoot(), new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    binding.searchText.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void afterTextChanged(Editable editable) {
            if (editor == null) {
              return;
            }
            search(binding.searchText.getText().toString());
          }

          @Override
          public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

          @Override
          public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
        });

    binding.gotoLast.setOnClickListener(this);
    binding.gotoNext.setOnClickListener(this);
    binding.replace.setOnClickListener(this);
    binding.replaceAll.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (id == binding.gotoLast.getId()) {
      gotoLast();
    } else if (id == binding.gotoNext.getId()) {
      gotoNext();
    } else if (id == binding.replace.getId()) {
      replace();
    } else if (id == binding.replaceAll.getId()) {
      replaceAll();
    }
  }

  public void showAndHide() {
    if (isShowing) {
      binding.getRoot().setVisibility(View.GONE);
      isShowing = false;
    } else {
      binding.getRoot().setVisibility(View.VISIBLE);
      isShowing = true;
    }
    if (searcher == null) {
      return;
    }
    searcher.stopSearch();
    binding.replaceText.setText("");
    binding.searchText.setText("");
  }

  private void search(String text) {
    if (!text.isEmpty()) {
      searcher.search(text, searchOptions);
    } else {
      searcher.stopSearch();
    }
  }

  private void gotoLast() {
    try {
      searcher.gotoPrevious();
    } catch (IllegalStateException e) {
      e.printStackTrace();
    }
  }

  private void gotoNext() {
    try {
      searcher.gotoNext();
    } catch (IllegalStateException e) {
      e.printStackTrace();
    }
  }

  private void replace() {
    try {
      searcher.replaceThis(binding.replaceText.getText().toString());
    } catch (IllegalStateException e) {
      e.printStackTrace();
    }
  }

  private void replaceAll() {
    try {
      searcher.replaceAll(binding.replaceText.getText().toString());
    } catch (IllegalStateException e) {
      e.printStackTrace();
    }
  }
}
