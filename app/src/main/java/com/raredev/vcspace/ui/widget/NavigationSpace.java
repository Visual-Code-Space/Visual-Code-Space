package com.raredev.vcspace.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import com.google.android.material.textview.MaterialTextView;
import com.raredev.vcspace.R;

public class NavigationSpace extends LinearLayout {
    
    public NavigationSpace(Activity context) {
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
    
    public void addItem(Activity activity, CharSequence title, int icon, View.OnClickListener listener) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.navigation_item, this, false);
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
