package com.raredev.vcspace.ui.panels;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import com.raredev.vcspace.util.Utils;

public class TextPanel extends Panel {

  private ScrollView scroll;
  private TextView text;

  public TextPanel(Context context) {
    super(context);
  }

  @Override
  public View createView() {
    scroll = new ScrollView(getContext());
    scroll.setLayoutParams(
        new ScrollView.LayoutParams(
            ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.MATCH_PARENT));

    text = new TextView(getContext());
    int padding = Utils.pxToDp(8);
    text.setPadding(padding, padding, padding, padding);
    text.setLayoutParams(
        new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    text.setTextSize(14);

    scroll.addView(text);
    return scroll;
  }

  @Override
  public void destroy() {
    scroll = null;
    text = null;
  }

  @Override
  public void unselected() {}

  @Override
  public void selected() {}

  public void setText(String text) {
    if (isViewCreated()) this.text.setText(text);
  }
}
