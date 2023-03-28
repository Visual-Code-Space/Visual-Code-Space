package com.raredev.vcspace.ui.editor;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.widget.ImageButton;
import androidx.appcompat.content.res.AppCompatResources;
import com.raredev.vcspace.util.Utils;
import io.github.rosemoe.sora.R;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.component.EditorTextActionWindow;

public class EditorTextActions extends EditorTextActionWindow {

  public EditorTextActions(CodeEditor editor) {
    super(editor);

    applyBackground();

    ImageButton selectAll = getView().findViewById(R.id.panel_btn_select_all);
    ImageButton cut = getView().findViewById(R.id.panel_btn_cut);
    ImageButton copy = getView().findViewById(R.id.panel_btn_copy);
    ImageButton pasteBtn = getView().findViewById(R.id.panel_btn_paste);

    updateImageTint(selectAll, editor.getContext());
    updateImageTint(cut, editor.getContext());
    updateImageTint(copy, editor.getContext());
    updateImageTint(pasteBtn, editor.getContext());
    
    selectAll.setBackground(getRippleEffectDrawable());
    cut.setBackground(getRippleEffectDrawable());
    copy.setBackground(getRippleEffectDrawable());
    pasteBtn.setBackground(getRippleEffectDrawable());
  }

  private void updateImageTint(ImageButton imageButton, Context context) {
    Drawable drawable = imageButton.getDrawable();
    ColorFilter colorFilter =
        new PorterDuffColorFilter(
            getResolvedColor(com.google.android.material.R.attr.colorPrimary),
            PorterDuff.Mode.SRC_IN);
    drawable.setTintList(
        ColorStateList.valueOf(getResolvedColor(com.google.android.material.R.attr.colorPrimary)));
    drawable.setColorFilter(colorFilter);
  }

  protected void applyBackground() {
    GradientDrawable drawable = new GradientDrawable();
    drawable.setShape(GradientDrawable.RECTANGLE);
    drawable.setCornerRadius(Utils.pxToDp(getEditor().getContext(), 10));
    drawable.setColor(
        ColorStateList.valueOf(getResolvedColor(com.google.android.material.R.attr.colorSurface)));
    drawable.setStroke(2, getResolvedColor(com.google.android.material.R.attr.colorOutline));
    getView().setBackground(drawable);
  }

  private int getResolvedColor(int attr) {
    TypedValue typedValue = new TypedValue();
    Resources.Theme theme = getEditor().getContext().getTheme();
    theme.resolveAttribute(attr, typedValue, true);
    return typedValue.data;
  }

  private Drawable getRippleEffectDrawable() {
    return AppCompatResources.getDrawable(
        getEditor().getContext(), com.raredev.vcspace.R.drawable.ripple_effect);
  }
}
