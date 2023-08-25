package com.raredev.vcspace.ui.panels;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import com.raredev.vcspace.util.Utils;

public class TextPanel extends Panel {

  private ScrollView scroll;
  private TextView text;

  public static TextPanel newTextPanel(Context context, String text) {
    TextPanel textPanel = new TextPanel(context);
    textPanel.setText(text);
    return textPanel;
  }

  public TextPanel(Context context) {
    super(context);
    scroll = new ScrollView(context);
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

    setContentView(scroll);
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
    this.text.setText(text);
  }
}
