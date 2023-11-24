package com.raredev.vcspace.adapters

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.SizeUtils
import com.raredev.vcspace.editor.completion.VCSpaceCompletionItem
import com.raredev.vcspace.editor.databinding.LayoutCompletionItemBinding
import io.github.rosemoe.sora.widget.component.EditorCompletionAdapter

class CompletionListAdapter : EditorCompletionAdapter() {

  override fun getItemHeight(): Int {
    return SizeUtils.dp2px(50f)
  }

  override fun getView(pos: Int, v: View?, parent: ViewGroup?, isSelected: Boolean): View {
    val binding =
        v?.let { LayoutCompletionItemBinding.bind(it) }
            ?: LayoutCompletionItemBinding.inflate(LayoutInflater.from(context), parent, false)

    val item = getItem(pos) as VCSpaceCompletionItem

    val kind = item.completionKind.toString()

    binding.apply {
      itemIcon.text = kind[0].toString()
      itemType.text = kind

      if (!TextUtils.isEmpty(item.label)) {
        itemLabel.text = item.label
        itemDesc.text = item.label
      }
      if (!TextUtils.isEmpty(item.desc)) {
        itemDesc.text = item.desc
      }
    }
    return binding.root
  }
}
