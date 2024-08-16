package com.raredev.vcspace.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.R
import com.raredev.vcspace.databinding.LayoutPathItemBinding
import com.raredev.vcspace.utils.getAttrColor
import com.raredev.vcspace.viewmodel.FileExplorerViewModel
import java.io.File

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

      val colorPrimary = root.context.getAttrColor(R.attr.colorPrimary)
      val colorControlNormal = root.context.getAttrColor(R.attr.colorControlNormal)

      name.text = file.name
      if (position == itemCount - 1) {
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

  @SuppressLint("NotifyDataSetChanged")
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

    paths.reverse()
    notifyDataSetChanged()
  }
}
