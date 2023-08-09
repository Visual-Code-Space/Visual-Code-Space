package com.raredev.vcspace.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.Toast;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.elevation.SurfaceColors;
import com.raredev.vcspace.databinding.LayoutSearcherBinding;
import com.raredev.vcspace.util.Utils;
import io.github.rosemoe.sora.widget.EditorSearcher;

public class SearcherPopupWindow implements View.OnClickListener {

  private LayoutSearcherBinding binding;

  private PopupWindow window;

  private Context context;
  private View view;

  private int currentX, currentY;

  private EditorSearcher.SearchOptions searchOptions =
      new EditorSearcher.SearchOptions(true, false);
  private EditorSearcher searcher;

  public SearcherPopupWindow(Context context, View view) {
    this.context = context;
    this.view = view;
    init();
  }

  private void init() {
    binding = LayoutSearcherBinding.inflate(LayoutInflater.from(context));
    window = new PopupWindow(context);

    window.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
    window.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

    window.setOutsideTouchable(true);
    window.setFocusable(true);
    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    window.setElevation(5);

    window.setContentView(binding.getRoot());

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
                window.update(currentX, currentY, -1, -1);
                break;
            }
            return true;
          }
        });

    binding.close.setOnClickListener(
        v -> {
          dismiss();
        });

    applyBackground();

    initSearcher();
  }

  private void initSearcher() {
    binding.searchText.setFocusable(true);
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
    if (isShowing()) {
      dismiss();
    } else {
      show();
    }
    searcher.stopSearch();
  }

  public boolean isShowing() {
    return window.isShowing();
  }

  public void bindSearcher(EditorSearcher searcher) {
    this.searcher = searcher;
  }

  public void show() {
    window.showAtLocation(view, Gravity.CENTER, 0, 0);
  }

  public void dismiss() {
    window.dismiss();
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

  private void applyBackground() {
    GradientDrawable drawable = new GradientDrawable();
    drawable.setShape(GradientDrawable.RECTANGLE);
    drawable.setCornerRadius(Utils.pxToDp(9));
    drawable.setColor(SurfaceColors.SURFACE_0.getColor(context));
    drawable.setStroke(
        2, MaterialColors.getColor(context, com.google.android.material.R.attr.colorOutline, 0));
    binding.getRoot().setBackground(drawable);
  }
}
