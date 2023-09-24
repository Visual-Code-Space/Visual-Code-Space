package com.raredev.vcspace.editor.completion;

import android.graphics.drawable.GradientDrawable;
import com.google.android.material.R;
import com.google.android.material.color.MaterialColors;
import com.raredev.vcspace.BaseApp;
import io.github.rosemoe.sora.widget.component.DefaultCompletionLayout;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class CustomCompletionLayout extends DefaultCompletionLayout {

  @Override
  public void onApplyColorScheme(EditorColorScheme colorScheme) {
    var context = getCompletionList().getContext();
    var drawable = new GradientDrawable();
    drawable.setColor(MaterialColors.getColor(context, R.attr.colorSurface, 0));
    drawable.setStroke(2, MaterialColors.getColor(context, R.attr.colorOutline, 0));
    drawable.setCornerRadius(10);
    getCompletionList().setBackground(drawable);
  }
}
