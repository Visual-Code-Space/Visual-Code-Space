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

package com.teixeira.vcspace.editor.completion

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import com.blankj.utilcode.util.SizeUtils
import com.teixeira.vcspace.editor.databinding.LayoutCompletionItemBinding
import io.github.rosemoe.sora.lang.completion.SimpleCompletionItem
import io.github.rosemoe.sora.widget.component.EditorCompletionAdapter

class CompletionListAdapter : EditorCompletionAdapter() {

    override fun getItemHeight(): Int {
        return SizeUtils.dp2px(44f)
    }

    override fun getView(pos: Int, v: View?, parent: ViewGroup?, isSelected: Boolean): View {
        val binding = v?.let {
            LayoutCompletionItemBinding.bind(it)
        } ?: LayoutCompletionItemBinding.inflate(LayoutInflater.from(context), parent, false)

        val item = getItem(pos)
        val kind = when (item) {
            is VCSpaceCompletionItem -> item.completionKind.toString()
            is SimpleCompletionItem -> item.desc
            else -> CompletionItemKind.IDENTIFIER.name
        }

        binding.apply {
            itemIcon.text = kind[0].toString().uppercase()
            itemType.text = kind.toString().uppercase()

            if (!TextUtils.isEmpty(item.label)) {
                itemLabel.text = item.label
                itemDesc.text = item.label
            }
            if (!TextUtils.isEmpty(item.desc)) {
                itemDesc.text = item.desc
            }

            root.updatePadding(
                top = SizeUtils.dp2px(4f),
                bottom = SizeUtils.dp2px(4f)
            )

//      root.setOnLongClickListener {
//        PopupWindow(context).apply {
//          setBackgroundDrawable(GradientDrawable().apply {
//            setStroke(
//              2,
//              context.getAttrColor(com.google.android.material.R.attr.colorOutline),
//            )
//            setColor(Color(0xFF212121).copy(alpha = 0.9f).toArgb())
//            setCornerRadius(10f)
//          })
//          contentView = LinearLayout(context).apply {
//            layoutParams = ViewGroup.LayoutParams(
//              ViewGroup.LayoutParams.WRAP_CONTENT,
//              ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//
//            addView(TextView(context).apply {
//              layoutParams = ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//              )
//
//              updateLayoutParams<ViewGroup.LayoutParams> {
//                setPadding(SizeUtils.dp2px(20f))
//              }
//              text = item.label
//            })
//          }
//
//          isOutsideTouchable = true
//        }.showAsDropDown(root)
//        true
//      }
        }
        return binding.root
    }
}
