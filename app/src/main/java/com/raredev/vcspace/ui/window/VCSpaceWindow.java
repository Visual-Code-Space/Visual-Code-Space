package com.raredev.vcspace.ui.window;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.elevation.SurfaceColors;
import com.raredev.vcspace.databinding.LayoutWindowBaseBinding;
import com.raredev.vcspace.util.Utils;

public class VCSpaceWindow extends FrameLayout {

  private LayoutWindowBaseBinding binding;

  private int currentX, currentY;

  public VCSpaceWindow(Context context) {
    super(context);
    init();
  }

  private void init() {
    binding = LayoutWindowBaseBinding.inflate(LayoutInflater.from(getContext()));
    addView(binding.getRoot());
    setElevation(5);

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
    dismiss();
  }

  public void setTitle(int title) {
    binding.title.setText(title);
  }

  public void setTitle(String title) {
    binding.title.setText(title);
  }

  public boolean isShowing() {
    return getVisibility() == View.VISIBLE;
  }

  public void show() {
    setVisibility(View.VISIBLE);
    currentX = 0;
    currentY = 0;
    setX(0);
    setY(0);
  }

  public void dismiss() {
    setVisibility(View.GONE);
  }

  public void setContentView(View view) {
    binding.windowContainer.addView(view);
  }

  private void applyBackground() {
    GradientDrawable drawable = new GradientDrawable();
    drawable.setShape(GradientDrawable.RECTANGLE);
    drawable.setCornerRadius(Utils.pxToDp(9));
    drawable.setColor(SurfaceColors.SURFACE_0.getColor(getContext()));
    drawable.setStroke(
        2,
        MaterialColors.getColor(getContext(), com.google.android.material.R.attr.colorOutline, 0));
    setBackground(drawable);
  }
}
