package com.raredev.vcspace.ui.editor.completion;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.google.android.material.R;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.elevation.SurfaceColors;
import com.raredev.vcspace.util.ILogger;
import com.raredev.vcspace.util.Utils;
import io.github.rosemoe.sora.widget.component.CompletionLayout;
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class CustomCompletionLayout implements CompletionLayout {

  private EditorAutoCompletion mEditorAutoCompletion;
  private ListView mListView;
  private ProgressBar mProgressBar;
  private RelativeLayout mRelativeLayout;

  @Override
  public void setEditorCompletion(EditorAutoCompletion completion) {
    mEditorAutoCompletion = completion;
  }

  @Override
  public View inflate(Context context) {
    RelativeLayout layout = new RelativeLayout(context);
    mListView = new ListView(context);
    layout.addView(mListView, new LinearLayout.LayoutParams(-1, -1));
    mProgressBar = new ProgressBar(context);
    layout.addView(mProgressBar);
    var params = ((RelativeLayout.LayoutParams) mProgressBar.getLayoutParams());
    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    params.width = params.height = Utils.pxToDp(30);
    layout.setBackground(applyBackground(context));
    mRelativeLayout = layout;
    mListView.setDividerHeight(0);
    setLoading(true);
    mListView.setOnItemClickListener(
        (parent, view, position, id) -> {
          try {
            mEditorAutoCompletion.select(position);
          } catch (Exception e) {
            ILogger.error("EditorAutoCompletion", Log.getStackTraceString(e));
          }
        });

    return layout;
  }

  @Override
  public void onApplyColorScheme(EditorColorScheme colorScheme) {
    GradientDrawable gd = new GradientDrawable();
    gd.setCornerRadius(Utils.pxToDp(10));
    gd.setStroke(2, colorScheme.getColor(EditorColorScheme.COMPLETION_WND_CORNER));
    gd.setColor(colorScheme.getColor(EditorColorScheme.COMPLETION_WND_BACKGROUND));
    mRelativeLayout.setBackground(gd);
  }

  @Override
  public void setLoading(boolean state) {
    mProgressBar.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
  }

  @Override
  public ListView getCompletionList() {
    return mListView;
  }

  private void performScrollList(int offset) {
    ListView adpView = getCompletionList();

    long down = SystemClock.uptimeMillis();
    MotionEvent ev = MotionEvent.obtain(down, down, MotionEvent.ACTION_DOWN, 0f, 0f, 0);
    adpView.onTouchEvent(ev);
    ev.recycle();

    ev = MotionEvent.obtain(down, down, MotionEvent.ACTION_MOVE, 0f, offset, 0);
    adpView.onTouchEvent(ev);
    ev.recycle();

    ev = MotionEvent.obtain(down, down, MotionEvent.ACTION_CANCEL, 0f, offset, 0);
    adpView.onTouchEvent(ev);
    ev.recycle();
  }

  @Override
  public void ensureListPositionVisible(int position, int increment) {
    mListView.post(
        () -> {
          while (mListView.getFirstVisiblePosition() + 1 > position
              && mListView.canScrollList(-1)) {
            performScrollList(increment / 2);
          }
          while (mListView.getLastVisiblePosition() - 1 < position && mListView.canScrollList(1)) {
            performScrollList(-increment / 2);
          }
        });
  }

  private GradientDrawable applyBackground(Context context) {
    GradientDrawable drawable = new GradientDrawable();
    drawable.setShape(GradientDrawable.RECTANGLE);
    drawable.setCornerRadius(Utils.pxToDp(10));
    drawable.setColor(SurfaceColors.SURFACE_0.getColor(context));
    drawable.setStroke(2, MaterialColors.getColor(context, R.attr.colorOnSurface, 0));
    return drawable;
  }
}
