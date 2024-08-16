package com.teixeira.vcspace.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teixeira.vcspace.databinding.LayoutSheetOptionItemBinding
import com.teixeira.vcspace.models.SheetOptionItem

class SheetOptionsListAdapter(
  private val options: List<SheetOptionItem>,
  private val listener: (SheetOptionItem) -> Unit = {}
) : RecyclerView.Adapter<SheetOptionsListAdapter.VH>() {

  inner class VH(internal val binding: LayoutSheetOptionItemBinding) :
    RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    return VH(
      LayoutSheetOptionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
  }

  override fun onBindViewHolder(holder: VH, position: Int) {
    holder.binding.apply {
      val option = options[position]

      icon.setImageResource(option.icon)
      name.text = option.name

      root.setOnClickListener { listener.invoke(option) }
    }
  }

  override fun getItemCount(): Int {
    return options.size
  }
}
