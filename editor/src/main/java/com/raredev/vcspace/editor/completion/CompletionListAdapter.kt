/*
 * This file is part of Visual Code Space.
 *
 * Visual Code Space is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Visual Code Space is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Visual Code Space.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.raredev.vcspace.editor.completion

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.SizeUtils
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
