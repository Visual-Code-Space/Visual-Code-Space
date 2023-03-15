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

public class VCSpaceSearcher extends LinearLayout {
  private LayoutSearcherBinding binding;
  private CodeEditor editor;

  public boolean isShowing = false;

  public VCSpaceSearcher(Context context) {
    this(context, null);
  }

  public VCSpaceSearcher(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public VCSpaceSearcher(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    binding = LayoutSearcherBinding.inflate(LayoutInflater.from(getContext()));
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

    binding.gotoLast.setOnClickListener(
        (v) -> {
          gotoLast();
        });

    binding.gotoNext.setOnClickListener(
        (v) -> {
          gotoNext();
        });

    binding.replace.setOnClickListener(
        (v) -> {
          replace();
        });

    binding.replaceAll.setOnClickListener(
        (v) -> {
          replaceAll();
        });
  }

  public void bindEditor(@Nullable CodeEditor editor) {
    this.editor = editor;
  }

  public void showAndHide() {
    if (isShowing) {
      hide();
    } else {
      setVisibility(View.VISIBLE);
      isShowing = true;
    }
    if (editor == null) {
      return;
    }
    editor.getSearcher().stopSearch();
    binding.replaceText.setText("");
    binding.searchText.setText("");
  }

  public void hide() {
    setVisibility(View.GONE);
    isShowing = false;
  }

  private void search(String text) {
    if (!binding.searchText.getText().toString().isEmpty()) {
      editor
          .getSearcher()
          .search(text, new EditorSearcher.SearchOptions(true, true));
    } else {
      editor.getSearcher().stopSearch();
    }
  }

  private void gotoLast() {
    try {
      editor.getSearcher().gotoPrevious();
    } catch (IllegalStateException e) {
      e.printStackTrace();
    }
  }

  private void gotoNext() {
    try {
      editor.getSearcher().gotoNext();
    } catch (IllegalStateException e) {
      e.printStackTrace();
    }
  }

  private void replace() {
    try {
      editor.getSearcher().replaceThis(binding.replaceText.getText().toString());
    } catch (IllegalStateException e) {
      e.printStackTrace();
    }
  }

  private void replaceAll() {
    try {
      editor.getSearcher().replaceAll(binding.replaceText.getText().toString());
    } catch (IllegalStateException e) {
      e.printStackTrace();
    }
  }
}
