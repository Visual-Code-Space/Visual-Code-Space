package com.raredev.vcspace.editor.completion;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.blankj.utilcode.util.SizeUtils;
import com.raredev.vcspace.editor.databinding.LayoutCompletionItemBinding;
import io.github.rosemoe.sora.widget.component.EditorCompletionAdapter;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class CompletionItemAdapter extends EditorCompletionAdapter {

  @Override
  public int getItemHeight() {
    return SizeUtils.dp2px(50);
  }

  @Override
  protected View getView(int pos, View v, ViewGroup parent, boolean isCurrentCursorPosition) {
    LayoutCompletionItemBinding binding =
        LayoutCompletionItemBinding.inflate(LayoutInflater.from(getContext()), parent, false);

    if (isCurrentCursorPosition) {
      binding
          .getRoot()
          .setBackgroundColor(getThemeColor(EditorColorScheme.COMPLETION_WND_ITEM_CURRENT));
    }

    var item = getItem(pos);

    if (item.icon != null) {
      binding.itemIcon.setImageDrawable(item.icon);
    }

    if (item instanceof VCSpaceCompletionItem) {
      var vcspaceItem = (VCSpaceCompletionItem) item;
      if (!TextUtils.isEmpty(vcspaceItem.type)) {
        binding.itemType.setText(vcspaceItem.type);
      }
    }

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
