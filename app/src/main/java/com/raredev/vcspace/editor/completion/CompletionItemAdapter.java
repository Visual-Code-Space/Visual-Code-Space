package com.raredev.vcspace.editor.completion;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.raredev.vcspace.databinding.LayoutCompletionItemBinding;
import com.raredev.vcspace.util.Utils;
import io.github.rosemoe.sora.widget.component.EditorCompletionAdapter;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class CompletionItemAdapter extends EditorCompletionAdapter {
  private LayoutCompletionItemBinding binding;

  @Override
  public int getItemHeight() {
    return Utils.pxToDp(50);
  }

  @Override
  protected View getView(int pos, View v, ViewGroup parent, boolean isCurrentCursorPosition) {
    binding = LayoutCompletionItemBinding.inflate(LayoutInflater.from(getContext()), parent, false);

    if (isCurrentCursorPosition) {
      binding
          .getRoot()
          .setBackgroundColor(getThemeColor(EditorColorScheme.COMPLETION_WND_ITEM_CURRENT));
    }

    var item = (VCSpaceCompletionItem) getItem(pos);

    //
    // binding.itemLabel.setTextColor(getThemeColor(EditorColorScheme.COMPLETION_WND_TEXT_PRIMARY));
    //
    // binding.itemIcon.setTextColor(getThemeColor(EditorColorScheme.COMPLETION_WND_TEXT_PRIMARY));
    //
    // binding.itemDesc.setTextColor(getThemeColor(EditorColorScheme.COMPLETION_WND_TEXT_SECONDARY));

    if (!TextUtils.isEmpty(item.label)) {
      binding.itemLabel.setText(item.label);
    }

    if (!TextUtils.isEmpty(item.type)) {
      binding.itemIcon.setImageDrawable(item.icon);
      binding.itemType.setText(item.type);
    } else if (!TextUtils.isEmpty(item.desc)) {
      binding.itemIcon.setImageDrawable(item.icon);
    }

    if (!TextUtils.isEmpty(item.desc)) {
      binding.itemDesc.setText(item.desc);
    }
    return binding.getRoot();
  }
}
