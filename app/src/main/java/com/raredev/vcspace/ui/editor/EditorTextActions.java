package com.raredev.vcspace.ui.editor;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.widget.ImageButton;
import androidx.appcompat.content.res.AppCompatResources;
import com.google.android.material.color.MaterialColors;
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
  }

  private void updateImageTint(ImageButton imageButton, Context context) {
    final var colorPrimary =
        MaterialColors.getColor(
            getEditor().getContext(), com.google.android.material.R.attr.colorPrimary, 0);

    Drawable drawable = imageButton.getDrawable();
    ColorFilter colorFilter = new PorterDuffColorFilter(colorPrimary, PorterDuff.Mode.SRC_IN);
    drawable.setTintList(ColorStateList.valueOf(colorPrimary));
    drawable.setColorFilter(colorFilter);

    imageButton.setBackground(getRippleEffectDrawable());
  }

  protected void applyBackground() {
    final var colorSurface =
        MaterialColors.getColor(
            getEditor().getContext(), com.google.android.material.R.attr.colorSurface, 0);
    final var colorOutline =
        MaterialColors.getColor(
            getEditor().getContext(), com.google.android.material.R.attr.colorOutline, 0);

    GradientDrawable drawable = new GradientDrawable();
    drawable.setShape(GradientDrawable.RECTANGLE);
    drawable.setCornerRadius(Utils.pxToDp(10));
    drawable.setColor(ColorStateList.valueOf(colorSurface));
    drawable.setStroke(2, colorOutline);
    getView().setBackground(drawable);
  }

  private Drawable getRippleEffectDrawable() {
    return AppCompatResources.getDrawable(
        getEditor().getContext(), com.raredev.vcspace.R.drawable.ripple_effect);
  }
}
