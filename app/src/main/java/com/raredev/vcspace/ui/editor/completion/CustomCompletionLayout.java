package com.raredev.vcspace.ui.editor.completion;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import io.github.rosemoe.sora.widget.component.CompletionLayout;
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class CustomCompletionLayout implements CompletionLayout {

  private EditorAutoCompletion mEditorAutoCompletion;
  private ListView mListView;

  @Override
  public void setEditorCompletion(EditorAutoCompletion completion) {
    mEditorAutoCompletion = completion;
  }

  @Override
  public View inflate(Context context) {
    RelativeLayout layout = new RelativeLayout(context);

    layout.setBackground(applyBackground(context));

    mListView = new ListView(context);
    mListView.setDividerHeight(0);
    layout.addView(mListView, new LinearLayout.LayoutParams(-1, -1));
    mListView.setOnItemClickListener(
        (adapterView, view, position, l) -> mEditorAutoCompletion.select(position));
    return layout;
  }

  @Override
  public void onApplyColorScheme(EditorColorScheme colorScheme) {}

  @Override
  public void setLoading(boolean state) {}

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
    drawable.setCornerRadius(15f);
    drawable.setColor(
        ColorStateList.valueOf(
            getResolvedColor(context, com.google.android.material.R.attr.colorSurface)));
    drawable.setStroke(
        2, getResolvedColor(context, com.google.android.material.R.attr.colorOutline));
    return drawable;
  }

  private int getResolvedColor(Context context, int attr) {
    TypedValue typedValue = new TypedValue();
    Resources.Theme theme = context.getTheme();
    theme.resolveAttribute(attr, typedValue, true);
    return typedValue.data;
  }
}
