package com.raredev.vcspace.ui.panels;

import android.content.Context;
import android.widget.TextView;
import com.raredev.vcspace.util.Utils;

public class TextPanel extends Panel {

  private TextView text;
  
  public static TextPanel newTextPanel(Context context, String text) {
    TextPanel textPanel = new TextPanel(context);
    textPanel.setText(text);
    return textPanel;
  }

  public TextPanel(Context context) {
    super(context);
    text = new TextView(getContext());
    int padding = Utils.pxToDp(8);
    text.setPadding(padding, padding, padding, padding);
    text.setTextSize(14);

    setContentView(text);
  }

  @Override
  public void destroy() {
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
