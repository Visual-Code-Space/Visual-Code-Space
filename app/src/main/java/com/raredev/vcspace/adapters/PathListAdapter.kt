package com.raredev.vcspace.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.R
import com.google.android.material.color.MaterialColors
import com.raredev.vcspace.databinding.LayoutPathItemBinding
import com.raredev.vcspace.viewmodel.FileExplorerViewModel
import java.io.File
import java.util.Collections

class PathListAdapter : RecyclerView.Adapter<PathListAdapter.VH>() {

  private val paths: MutableList<File> = ArrayList()

  private var viewModel: FileExplorerViewModel? = null

  inner class VH(internal val binding: LayoutPathItemBinding) :
      RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    return VH(LayoutPathItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
  }

  override fun onBindViewHolder(holder: VH, position: Int) {
    holder.binding.apply {
      val file = paths[position]

      val colorPrimary = MaterialColors.getColor(root.context, R.attr.colorPrimary, 0)
      val colorControlNormal = MaterialColors.getColor(root.context, R.attr.colorControlNormal, 0)

      name.text = file.name
      if (position == getItemCount() - 1) {
        name.setTextColor(colorPrimary)
        separator.visibility = View.GONE
      } else {
        name.setTextColor(colorControlNormal)
        separator.visibility = View.VISIBLE
      }
      name.setOnClickListener { viewModel?.setCurrentPath(file.absolutePath) }
    }
  }

  override fun getItemCount(): Int {
    return paths.size
  }

  fun setFileExplorerViewModel(viewModel: FileExplorerViewModel) {
    this.viewModel = viewModel
  }

  fun setPath(path: File?) {
    paths.clear()

    var temp: File? = path
    while (temp != null) {
      if (temp.absolutePath.equals("/storage/emulated")) {
        break
      }
      paths.add(temp)
      temp = temp.parentFile
    }

    Collections.reverse(paths)
    notifyDataSetChanged()
  }
}
