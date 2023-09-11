package com.raredev.vcspace.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.google.android.material.textview.MaterialTextView;
import com.raredev.vcspace.R;

public class NavigationSpace extends LinearLayout {

  public NavigationSpace(Context context) {
    super(context);
    init();
  }

  public NavigationSpace(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public void init() {
    setOrientation(HORIZONTAL);
    setGravity(Gravity.CENTER);
  }

  public void addItem(Context context, int title, int icon, View.OnClickListener listener) {
    addItem(context, context.getString(title), icon, listener);
  }

  public void addItem(
      Context context, CharSequence title, int icon, View.OnClickListener listener) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View view = inflater.inflate(R.layout.layout_navigation_item, this, false);
    view.setOnClickListener(listener);
    view.setTooltipText(title);

    ImageView imgIcon = view.findViewById(R.id.icon);
    imgIcon.setImageResource(icon);

    MaterialTextView tvTitle = view.findViewById(R.id.title);
    tvTitle.setText(title);

    addView(view);
  }

  public void clear() {
    removeAllViews();
  }
}
