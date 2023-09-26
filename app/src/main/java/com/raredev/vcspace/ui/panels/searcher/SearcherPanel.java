package com.raredev.vcspace.ui.panels.searcher;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.widget.TooltipCompat;
import com.google.android.material.color.MaterialColors;
import com.raredev.vcspace.databinding.LayoutSearcherPanelBinding;
import com.raredev.vcspace.events.PanelEvent;
import com.raredev.vcspace.events.UpdateSearcherEvent;
import com.raredev.vcspace.res.R;
import com.raredev.vcspace.ui.panels.FloatingPanelArea;
import com.raredev.vcspace.ui.panels.Panel;
import com.raredev.vcspace.utils.PreferencesUtils;
import com.raredev.vcspace.utils.Utils;
import io.github.rosemoe.sora.widget.EditorSearcher;

public class SearcherPanel extends Panel implements View.OnClickListener {

  private LayoutSearcherPanelBinding binding;

  private EditorSearcher.SearchOptions searchOptions;
  private EditorSearcher searcher;

  public static FloatingPanelArea createFloating(Context context, FrameLayout parent) {
    FloatingPanelArea floatingPanel = new FloatingPanelArea(context, parent);
    floatingPanel.addPanel(new SearcherPanel(context), true);
    return floatingPanel;
  }

  public SearcherPanel(Context context) {
    super(context, R.string.search);
  }

  @Override
  public View createView() {
    binding = LayoutSearcherPanelBinding.inflate(LayoutInflater.from(getContext()));
    return binding.getRoot();
  }

  @Override
  public void viewCreated(View view) {
    super.viewCreated(view);
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

    TooltipCompat.setTooltipText(
        binding.ignoreLetterCase, getContext().getString(R.string.ignore_letter_case));
    TooltipCompat.setTooltipText(binding.useRegex, getContext().getString(R.string.use_regex));
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

  @Override
  public void destroy() {
    binding = null;
  }

  @Override
  public void unselected() {}

  @Override
  public void selected() {
    if (binding == null) return;
    search(binding.searchText.getText().toString());
  }

  @Override
  public void receiveEvent(PanelEvent event) {
    if (event instanceof UpdateSearcherEvent) {
      this.searcher = ((UpdateSearcherEvent) event).getSearcher();
      search(binding.searchText.getText().toString());
    }
  }

  public void updateSearchOptions() {
    var prefs = PreferencesUtils.getDefaultPrefs();
    var ignoreCase = prefs.getBoolean("searcher_ignoreLetterCase", true);
    var useRegex = prefs.getBoolean("searcher_useRegex", false);

    var colorPrimary =
        MaterialColors.getColor(getContext(), com.google.android.material.R.attr.colorPrimary, 0);
    var colorControlNormal =
        MaterialColors.getColor(
            getContext(), com.google.android.material.R.attr.colorControlNormal, 0);

    Utils.updateImageTint(binding.ignoreLetterCase, ignoreCase ? colorPrimary : colorControlNormal);
    Utils.updateImageTint(binding.useRegex, useRegex ? colorPrimary : colorControlNormal);

    searchOptions = new EditorSearcher.SearchOptions(ignoreCase, useRegex);
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
