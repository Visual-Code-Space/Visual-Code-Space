package com.raredev.vcspace.ui.panels;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.blankj.utilcode.util.SizeUtils;
import com.google.android.material.color.MaterialColors;
import com.raredev.vcspace.res.R;

public class FloatingPanelArea extends PanelArea {

  private FloatingPanel panel;

  private boolean resizeable = true;
  private boolean showing = true;

  private int minWidth;
  private int minHeight;

  private int initialWidth;
  private int initialHeight;

  public FloatingPanelArea(Context context, FrameLayout parent) {
    this(context, parent, SizeUtils.dp2px(390), SizeUtils.dp2px(210));
  }

  public FloatingPanelArea(
      Context context, FrameLayout parent, int initialWidth, int initialHeight) {
    super(context, parent);
    this.initialWidth = initialWidth;
    this.initialHeight = initialHeight;

    minWidth = SizeUtils.dp2px(40f);
    minHeight = minWidth;
    init();
  }

  private void init() {
    panel = new FloatingPanel(binding.getRoot());

    binding
        .getRoot()
        .getViewTreeObserver()
        .addOnGlobalLayoutListener(
            new ViewTreeObserver.OnGlobalLayoutListener() {
              @Override
              public void onGlobalLayout() {
                if (!isShowing()) {
                  binding.getRoot().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                  return;
                }
                updatePositionAndSize();
              }
            });
    resizePanel(initialWidth, initialHeight);

    addFloatingPanelTopBar();
    addEdgeViews();

    setFixedPanels(true);
    applyBackground();
  }

  @Override
  public boolean removePanel(Panel panel) {
    var removed = super.removePanel(panel);
    if (panels.isEmpty() && isShowing()) close();
    return removed;
  }

  private void updatePositionAndSize() {
    //resizePanel(panel.getWidth(), panel.getHeight());
    repositionPanel(panel.getX(), panel.getY());
  }

  public boolean isShowing() {
    return showing;
  }

  public void show() {
    if (!isShowing()) {
      parent.addView(binding.getRoot());
      showing = true;
    }
  }

  public void close() {
    if (isShowing()) {
      showing = false;
      if (listener != null) {
        listener.removedPanel(this);
      }

      binding
          .getRoot()
          .animate()
          .scaleY(0f)
          .alpha(0f)
          .setDuration(150)
          .setInterpolator(new AccelerateDecelerateInterpolator())
          .withEndAction(
              () -> {
                removeAllPanels();
                parent.removeView(binding.getRoot());
              })
          .start();
    }
  }

  private void addFloatingPanelTopBar() {
    int padding = SizeUtils.dp2px(2);
    int margins = SizeUtils.dp2px(4);
    var move = new ImageView(context);
    var moveLayoutParams = new LinearLayout.LayoutParams(SizeUtils.dp2px(25), SizeUtils.dp2px(25));
    moveLayoutParams.leftMargin = margins;
    moveLayoutParams.rightMargin = margins;
    move.setLayoutParams(moveLayoutParams);
    move.setImageResource(R.drawable.gesture_tap);
    move.setPadding(padding, padding, padding, padding);
    move.setOnTouchListener(getMoveTouchListener());

    var close = new ImageView(context);
    var closeLayoutParams = new LinearLayout.LayoutParams(SizeUtils.dp2px(25), SizeUtils.dp2px(25));
    closeLayoutParams.leftMargin = margins;
    closeLayoutParams.rightMargin = margins + 5;
    close.setLayoutParams(closeLayoutParams);
    close.setImageResource(R.drawable.close);
    close.setPadding(padding, padding, padding, padding);
    close.setOnClickListener((v) -> close());

    addViewInTopbar(move);
    addViewInTopbar(close);
  }

  private void addEdgeViews() {
    // Right side
    View rightEdge = new View(context);
    RelativeLayout.LayoutParams rightParams =
        new RelativeLayout.LayoutParams(
            SizeUtils.dp2px(9), RelativeLayout.LayoutParams.MATCH_PARENT);
    rightParams.addRule(RelativeLayout.ALIGN_PARENT_END);
    binding.getRoot().addView(rightEdge, rightParams);

    // Lower part
    View bottomEdge = new View(context);
    RelativeLayout.LayoutParams bottomParams =
        new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(9));
    bottomParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    binding.getRoot().addView(bottomEdge, bottomParams);

    rightEdge.setOnTouchListener(getRightEdgeTouchListener());
    bottomEdge.setOnTouchListener(getBottomEdgeTouchListener());
  }

  private void applyBackground() {
    GradientDrawable drawable = new GradientDrawable();
    drawable.setCornerRadius(10);
    drawable.setColor(
        MaterialColors.getColor(context, com.google.android.material.R.attr.colorSurface, 0));
    drawable.setStroke(
        2, MaterialColors.getColor(context, com.google.android.material.R.attr.colorOutline, 0));
    binding.getRoot().setBackground(drawable);
    binding.getRoot().setElevation(2.5f);
  }

  public View.OnTouchListener getRightEdgeTouchListener() {
    return new View.OnTouchListener() {
      private float startX;
      private int originalWidth;

      @Override
      public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
            startX = event.getRawX();
            originalWidth = panel.getWidth();
            break;
          case MotionEvent.ACTION_MOVE:
            float deltaX = event.getRawX() - startX;
            if (deltaX >= parent.getWidth()) break;

            int newWidth = (int) (originalWidth + deltaX);

            resizePanel(newWidth, panel.getHeight());
            break;
        }
        return true;
      }
    };
  }

  public View.OnTouchListener getBottomEdgeTouchListener() {
    return new View.OnTouchListener() {
      private float startY;
      private int originalHeight;

      @Override
      public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
            startY = event.getRawY();
            originalHeight = panel.getHeight();
            break;
          case MotionEvent.ACTION_MOVE:
            float deltaY = event.getRawY() - startY;

            int newHeight = originalHeight + (int) deltaY;

            resizePanel(panel.getWidth(), newHeight);
            break;
        }
        return true;
      }
    };
  }

  public View.OnTouchListener getMoveTouchListener() {
    return new View.OnTouchListener() {
      private float dx, dy;

      @Override
      public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
            dx = panel.getX() - event.getRawX();
            dy = panel.getY() - event.getRawY();
            break;
          case MotionEvent.ACTION_MOVE:
            int newX = (int) (event.getRawX() + dx);
            int newY = (int) (event.getRawY() + dy);

            repositionPanel(newX, newY);
            break;
        }
        return true;
      }
    };
  }

  private void resizePanel(int width, int height) {
    int maxWidth = width + (parent.getWidth() - (panel.getX() + width));
    int maxHeight = height + (parent.getHeight() - (panel.getY() + height));

    width = clamp(width, minWidth, maxWidth);
    height = clamp(height, minHeight, maxHeight);

    ViewGroup.LayoutParams params = panel.getLayoutParams();
    params.width = width;
    params.height = height;
    panel.setLayoutParams(params);
  }

  private void repositionPanel(int x, int y) {
    int maxX = parent.getWidth() - panel.getWidth();
    int maxY = parent.getHeight() - panel.getHeight();

    x = clamp(x, 0, maxX);
    y = clamp(y, 0, maxY);

    panel.setX(x);
    panel.setY(y);
  }

  private int clamp(int value, int min, int max) {
    return Math.max(min, Math.min(max, value));
  }

  class FloatingPanel {
    private View view;

    public FloatingPanel(View view) {
      this.view = view;
    }

    public void setX(int x) {
      view.setX(x);
    }

    public int getX() {
      return (int) view.getX();
    }

    public void setY(int y) {
      view.setY(y);
    }

    public int getY() {
      return (int) view.getY();
    }

    public int getWidth() {
      return view.getWidth();
    }

    public int getHeight() {
      return view.getHeight();
    }

    public ViewGroup.LayoutParams getLayoutParams() {
      return view.getLayoutParams();
    }

    public void setLayoutParams(ViewGroup.LayoutParams params) {
      view.setLayoutParams(params);
    }
  }
}
