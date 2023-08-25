package com.raredev.vcspace.ui.panels;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.elevation.SurfaceColors;
import com.raredev.vcspace.res.R;
import com.raredev.vcspace.util.Utils;

public class FloatingPanelArea extends PanelArea {

  private FloatingPanel panel;

  private boolean resizeable;
  private boolean showing = true;

  public FloatingPanelArea(Context context, FrameLayout parent) {
    super(context, parent);
    init();
  }

  private void init() {
    binding.getRoot().setClickable(true);
    panel = new FloatingPanel(binding.getRoot());

    var layoutParams = panel.getLayoutParams();
    if (layoutParams == null) {
      layoutParams = new ViewGroup.LayoutParams(Utils.pxToDp(390), Utils.pxToDp(210));
    } else {
      layoutParams.width = Utils.pxToDp(390);
      layoutParams.height = Utils.pxToDp(210);
    }
    panel.setLayoutParams(layoutParams);

    addFloatingPanelTopBar();
    addEdgeViews();

    applyBackground();
    show(true);
  }

  @Override
  public boolean removePanel(Panel panel) {
    var removed = super.removePanel(panel);
    if (panels.isEmpty() && isShowing()) close();
    return removed;
  }

  public boolean isShowing() {
    return showing;
  }

  public void show() {
    show(false);
  }

  public void show(boolean animate) {
    if (!isShowing()) {
      parent.addView(binding.getRoot());
      showing = true;

      if (animate) {
        binding.getRoot().setScaleX(0.5f);
        binding.getRoot().setScaleY(0.5f);
        binding.getRoot().setAlpha(0.0f);
        binding.getRoot().animate().scaleX(1).scaleY(1).alpha(1).setDuration(100).start();
      }
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
          .scaleX(0.5f)
          .scaleY(0.5f)
          .alpha(0.0f)
          .setDuration(100)
          .withEndAction(
              () -> {
                removeAllPanels();
                parent.removeView(binding.getRoot());
              })
          .start();
    }
  }

  private void addFloatingPanelTopBar() {
    int padding = Utils.pxToDp(2);
    int margins = Utils.pxToDp(4);
    var move = new ImageView(context);
    var moveLayoutParams = new LinearLayout.LayoutParams(Utils.pxToDp(25), Utils.pxToDp(25));
    moveLayoutParams.leftMargin = margins;
    moveLayoutParams.leftMargin = margins;
    move.setLayoutParams(moveLayoutParams);
    move.setImageResource(R.drawable.gesture_tap);
    move.setPadding(padding, padding, padding, padding);
    move.setOnTouchListener(getMoveTouchListener());

    var close = new ImageView(context);
    var closeLayoutParams = new LinearLayout.LayoutParams(Utils.pxToDp(25), Utils.pxToDp(25));
    closeLayoutParams.leftMargin = margins;
    closeLayoutParams.leftMargin = margins + 5;
    close.setLayoutParams(closeLayoutParams);
    close.setImageResource(R.drawable.close);
    close.setPadding(padding, padding, padding, padding);
    close.setOnClickListener((v) -> close());

    addViewInTopbar(move);
    addViewInTopbar(close);
  }

  private void addEdgeViews() {
    // Left side
    View leftEdge = new View(context);
    RelativeLayout.LayoutParams leftParams =
        new RelativeLayout.LayoutParams(Utils.pxToDp(9), RelativeLayout.LayoutParams.MATCH_PARENT);
    leftParams.addRule(RelativeLayout.ALIGN_PARENT_START);
    binding.getRoot().addView(leftEdge, leftParams);

    // Right side
    View rightEdge = new View(context);
    RelativeLayout.LayoutParams rightParams =
        new RelativeLayout.LayoutParams(Utils.pxToDp(9), RelativeLayout.LayoutParams.MATCH_PARENT);
    rightParams.addRule(RelativeLayout.ALIGN_PARENT_END);
    binding.getRoot().addView(rightEdge, rightParams);

    // Upper part
    View topEdge = new View(context);
    RelativeLayout.LayoutParams topParams =
        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Utils.pxToDp(9));
    topParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
    binding.getRoot().addView(topEdge, topParams);

    // Lower part
    View bottomEdge = new View(context);
    RelativeLayout.LayoutParams bottomParams =
        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Utils.pxToDp(9));
    bottomParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    binding.getRoot().addView(bottomEdge, bottomParams);

    leftEdge.setOnTouchListener(getLeftEdgeTouchListener());
    rightEdge.setOnTouchListener(getRightEdgeTouchListener());
    topEdge.setOnTouchListener(getTopEdgeTouchListener());
    bottomEdge.setOnTouchListener(getBottomEdgeTouchListener());
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

  public View.OnTouchListener getLeftEdgeTouchListener() {
    return new View.OnTouchListener() {
      private float startX;
      private int originalWidth;
      private int originalX;

      @Override
      public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
            startX = event.getRawX();
            originalWidth = panel.getWidth();
            originalX = (int) panel.getX();
            break;
          case MotionEvent.ACTION_MOVE:
            float deltaX = event.getRawX() - startX;
            int newWidth = (int) (originalWidth - deltaX);
            if (newWidth < 100) break;
            ViewGroup.LayoutParams params = panel.getLayoutParams();
            params.width = newWidth;
            panel.setLayoutParams(params);
            panel.currentX = (int) (originalX + (deltaX));
            panel.setX(panel.currentX);
            break;
        }
        return true;
      }
    };
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
            int newWidth = (int) (originalWidth + deltaX);
            if (newWidth < 100) break;
            ViewGroup.LayoutParams params = panel.getLayoutParams();
            params.width = newWidth;
            panel.setLayoutParams(params);
            break;
        }
        return true;
      }
    };
  }

  public View.OnTouchListener getTopEdgeTouchListener() {
    return new View.OnTouchListener() {
      private float startY;
      private int originalHeight;
      private int originalY;

      @Override
      public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
            startY = event.getRawY();
            originalHeight = panel.getHeight();
            originalY = (int) panel.getY();
            break;
          case MotionEvent.ACTION_MOVE:
            float deltaY = event.getRawY() - startY;
            int newHeight = (int) (originalHeight - deltaY);
            ViewGroup.LayoutParams params = panel.getLayoutParams();
            params.height = newHeight;
            panel.setLayoutParams(params);
            panel.currentY = (int) (originalY + deltaY);
            panel.setY(panel.currentY);
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
            ViewGroup.LayoutParams params = panel.getLayoutParams();
            params.height = newHeight;
            panel.setLayoutParams(params);
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
            dx = panel.currentX - event.getRawX();
            dy = panel.currentY - event.getRawY();
            break;
          case MotionEvent.ACTION_MOVE:
            int newX = (int) (event.getRawX() + dx);
            int newY = (int) (event.getRawY() + dy);

            int maxX = parent.getWidth() - panel.getWidth();
            int maxY = parent.getHeight() - panel.getHeight();

            if (newX < 0) newX = 0;
            if (newX > maxX) newX = maxX;
            if (newY < 0) newY = 0;
            if (newY > maxY) newY = maxY;

            panel.currentX = newX;
            panel.currentY = newY;
            panel.setX(newX);
            panel.setY(newY);
            break;
        }
        return true;
      }
    };
  }

  class FloatingPanel {
    public int currentX, currentY;
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
