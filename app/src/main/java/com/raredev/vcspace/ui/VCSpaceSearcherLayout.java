package com.raredev.vcspace.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.raredev.vcspace.databinding.LayoutSearcherBinding;
import io.github.rosemoe.sora.widget.EditorSearcher;

public class VCSpaceSearcherLayout extends LinearLayout implements View.OnClickListener {
  private LayoutSearcherBinding binding;

  private EditorSearcher.SearchOptions searchOptions =
      new EditorSearcher.SearchOptions(true, false);
  private EditorSearcher searcher;

  public boolean isShowing = false;

  public VCSpaceSearcherLayout(Context context, EditorSearcher searcher) {
    super(context);
    this.searcher = searcher;

    binding = LayoutSearcherBinding.inflate(LayoutInflater.from(getContext()));
    removeAllViews();
    
    binding.searchText.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void afterTextChanged(Editable editable) {
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
    
    binding.getRoot().setVisibility(View.GONE);
    addView(
        binding.getRoot(), new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
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
