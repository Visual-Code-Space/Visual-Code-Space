package com.raredev.vcspace.ui.editor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.raredev.common.util.Utils;
import com.raredev.vcspace.databinding.LayoutCompletionItemBinding;
import io.github.rosemoe.sora.widget.component.EditorCompletionAdapter;

public class CompletionItemAdapter extends EditorCompletionAdapter {
  private LayoutCompletionItemBinding binding;

  @Override
  public int getItemHeight() {
    return Utils.pxToDp(getContext(), 45);
  }

  @Override
  protected View getView(int pos, View v, ViewGroup parent, boolean isCurrentCursorPosition) {
    binding = LayoutCompletionItemBinding.inflate(LayoutInflater.from(getContext()));

    var item = getItem(pos);

    TextView tv = binding.itemLabel;
    tv.setText(item.label);

    tv = binding.itemDesc;
    tv.setText(item.desc);

    ImageView iv = binding.itemImage;
    iv.setImageDrawable(item.icon);
    return binding.getRoot();
  }
}
