package com.raredev.vcspace.ui;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.widget.TooltipCompat;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.elevation.SurfaceColors;
import com.raredev.vcspace.R;
import com.raredev.vcspace.databinding.LayoutSearcherBinding;
import com.raredev.vcspace.util.PreferencesUtils;
import com.raredev.vcspace.util.Utils;
import io.github.rosemoe.sora.widget.EditorSearcher;

public class SearcherPopupWindow extends FrameLayout implements View.OnClickListener {

  private LayoutSearcherBinding binding;

  private int currentX, currentY;

  private EditorSearcher.SearchOptions searchOptions;
  private EditorSearcher searcher;

  public SearcherPopupWindow(Context context) {
    super(context);
    init();
  }

  private void init() {
    binding = LayoutSearcherBinding.inflate(LayoutInflater.from(getContext()));
    setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    setElevation(5);
    addView(binding.getRoot());
    dismiss();

    binding.move.setOnTouchListener(
        new View.OnTouchListener() {
          private float dx, dy;

          @Override
          public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
              case MotionEvent.ACTION_DOWN:
                dx = currentX - event.getRawX();
                dy = currentY - event.getRawY();
                break;
              case MotionEvent.ACTION_MOVE:
                currentX = (int) (event.getRawX() + dx);
                currentY = (int) (event.getRawY() + dy);
                setX(currentX);
                setY(currentY);
                break;
            }
            return true;
          }
        });
    binding.close.setOnClickListener(v -> dismiss());

    applyBackground();

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

  public boolean isShowing() {
    return getVisibility() == View.VISIBLE;
  }

  public void show() {
    search(binding.searchText.getText().toString());
    setVisibility(View.VISIBLE);
    currentX = 0;
    currentY = 0;
    setX(0);
    setY(0);
  }

  public void dismiss() {
    setVisibility(View.GONE);
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

  private void applyBackground() {
    GradientDrawable drawable = new GradientDrawable();
    drawable.setShape(GradientDrawable.RECTANGLE);
    drawable.setCornerRadius(Utils.pxToDp(9));
    drawable.setColor(SurfaceColors.SURFACE_0.getColor(getContext()));
    drawable.setStroke(
        2, MaterialColors.getColor(getContext(), com.google.android.material.R.attr.colorOutline, 0));
    setBackground(drawable);
  }
}
