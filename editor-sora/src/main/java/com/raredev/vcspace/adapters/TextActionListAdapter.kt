package com.raredev.vcspace.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.raredev.vcspace.editor.TextActionsWindow
import com.raredev.vcspace.editor.databinding.LayoutTextActionItemBinding
import com.raredev.vcspace.models.TextAction
import com.raredev.vcspace.res.R

class TextActionListAdapter(
  val textActions: TextActionsWindow
): RecyclerView.Adapter<TextActionListAdapter.TextActionViewHolder>() {

  private val actions: MutableList<TextAction> = ArrayList()
  private val visibleActions: MutableList<TextAction> = ArrayList()

  init {
    ensureActions()
  }

  class TextActionViewHolder(
    internal val binding: LayoutTextActionItemBinding
  ): RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextActionViewHolder {
    return TextActionViewHolder(LayoutTextActionItemBinding.inflate(
      LayoutInflater.from(parent.context), parent, false
    ))
  }

  override fun onBindViewHolder(holder: TextActionViewHolder, position: Int) {
    holder.binding.item.apply {
      val action = visibleActions[position]

      setIconResource(action.icon)

      isClickable = action.clickable
      tooltipText = context.getString(action.text)

      setOnClickListener {
        textActions.executeTextAction(action)
      }
    }
  }

  override fun getItemCount(): Int {
    return visibleActions.size
  }

  fun updateAction(pos: Int, visible: Boolean, clickable: Boolean = true) {
    actions[pos].apply {
      this.visible = visible
      this.clickable = clickable
    }
  }

  fun refreshActions() {
    visibleActions.clear()
    for (action in actions) {
      if (action.visible) {
        visibleActions.add(action)
      }
    }
    notifyDataSetChanged()
  }

  private fun ensureActions() {
    actions.apply {
      add(TextAction(R.drawable.ic_comment_text_outline, R.string.comment_line))
      add(TextAction(R.drawable.ic_select_all, R.string.select_all))
      add(TextAction(R.drawable.ic_text_select_start, R.string.long_select))
      add(TextAction(R.drawable.ic_copy, R.string.copy))
      add(TextAction(R.drawable.ic_paste, R.string.paste))
      add(TextAction(R.drawable.ic_cut, R.string.cut))
      add(TextAction(R.drawable.ic_format_align_left, R.string.menu_format))
    }
  }
}
