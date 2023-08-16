package com.raredev.vcspace.ui.window;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.widget.TooltipCompat;
import com.google.android.material.color.MaterialColors;
import com.raredev.vcspace.R;
import com.raredev.vcspace.databinding.LayoutSearcherBinding;
import com.raredev.vcspace.util.PreferencesUtils;
import com.raredev.vcspace.util.Utils;
import io.github.rosemoe.sora.widget.EditorSearcher;

public class SearcherWindow extends VCSpaceWindow implements View.OnClickListener {

  private LayoutSearcherBinding binding;

  private EditorSearcher.SearchOptions searchOptions;
  private EditorSearcher searcher;

  public SearcherWindow(Context context) {
    super(context);
    init();
  }

  private void init() {
    binding = LayoutSearcherBinding.inflate(LayoutInflater.from(getContext()));
    setContentView(binding.getRoot());
    setTitle(R.string.search);
    initSearcher();
  }

  private void initSearcher() {
    binding.searchText.requestFocus();
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

    binding.ignoreLetterCase.setOnClickListener(
        v -> {
          var prefs = PreferencesUtils.getDefaultPrefs();
          var ignoreCase = prefs.getBoolean("searcher_ignoreLetterCase", true);
          prefs.edit().putBoolean("searcher_ignoreLetterCase", !ignoreCase).commit();
          updateSearchOptions();
        });
    
    binding.useRegex.setOnClickListener(
        v -> {
          var prefs = PreferencesUtils.getDefaultPrefs();
          var useRegex = prefs.getBoolean("searcher_useRegex", false);
          prefs.edit().putBoolean("searcher_useRegex", !useRegex).commit();
          updateSearchOptions();
        });

    binding.gotoLast.setOnClickListener(this);
    binding.gotoNext.setOnClickListener(this);
    binding.replace.setOnClickListener(this);
    binding.replaceAll.setOnClickListener(this);

    TooltipCompat.setTooltipText(binding.ignoreLetterCase,  getContext().getString(R.string.ignore_letter_case));
    updateSearchOptions();
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (searchOptions == null || searcher == null) return;
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
    if (isShowing()) {
      dismiss();
    } else {
      show();
    }
  }

  @Override
  public void show() {
    search(binding.searchText.getText().toString());
    super.show();
  }

  public void updateSearchOptions() {
    var prefs = PreferencesUtils.getDefaultPrefs();
    var ignoreCase = prefs.getBoolean("searcher_ignoreLetterCase", true);
    var useRegex = prefs.getBoolean("searcher_useRegex", false);
    if (ignoreCase) {
      Utils.updateImageTint(
          binding.ignoreLetterCase,
          MaterialColors.getColor(
              getContext(), com.google.android.material.R.attr.colorPrimary, 0));
    } else {
      Utils.updateImageTint(
          binding.ignoreLetterCase,
          MaterialColors.getColor(
              getContext(), com.google.android.material.R.attr.colorControlNormal, 0));
    }
    if (useRegex) {
      Utils.updateImageTint(
          binding.useRegex,
          MaterialColors.getColor(
              getContext(), com.google.android.material.R.attr.colorPrimary, 0));
    } else {
      Utils.updateImageTint(
          binding.useRegex,
          MaterialColors.getColor(
              getContext(), com.google.android.material.R.attr.colorControlNormal, 0));
    }

    searchOptions = new EditorSearcher.SearchOptions(ignoreCase, useRegex);
  }

  public void bindSearcher(EditorSearcher searcher) {
    this.searcher = searcher;

    search(binding.searchText.getText().toString());
  }

  private void search(String text) {
    if (searchOptions == null || searcher == null) return;
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
