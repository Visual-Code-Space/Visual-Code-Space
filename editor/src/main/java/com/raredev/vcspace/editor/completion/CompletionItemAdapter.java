package com.raredev.vcspace.editor.completion;

import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.blankj.utilcode.util.SizeUtils;
import com.google.android.material.R;
import com.google.android.material.color.MaterialColors;
import com.raredev.vcspace.editor.databinding.LayoutCompletionItemBinding;
import io.github.rosemoe.sora.widget.component.EditorCompletionAdapter;

public class CompletionItemAdapter extends EditorCompletionAdapter {

  @Override
  public int getItemHeight() {
    return SizeUtils.dp2px(50f);
  }

  @Override
  protected View getView(int pos, View v, ViewGroup parent, boolean isCurrentCursorPosition) {
    LayoutCompletionItemBinding binding =
        LayoutCompletionItemBinding.inflate(LayoutInflater.from(getContext()), parent, false);

    if (isCurrentCursorPosition) {
      var drawable = new GradientDrawable();
      drawable.setColor(MaterialColors.getColor(parent.getContext(), R.attr.colorControlHighlight, 0));
      drawable.setCornerRadius(10);
      binding.getRoot().setBackground(drawable);
    }

    var item = (VCSpaceCompletionItem) getItem(pos);

    var completionKind = item.completionKind;

    var type = completionKind == null ? "O" : completionKind.toString();

    binding.itemIcon.setText(String.valueOf(type.charAt(0)));
    binding.itemType.setText(type);

    if (!TextUtils.isEmpty(item.label)) {
      binding.itemLabel.setText(item.label);
      binding.itemDesc.setText(item.label);
    }

    if (!TextUtils.isEmpty(item.desc)) {
      binding.itemDesc.setText(item.desc);
    }
    return binding.getRoot();
  }
}
