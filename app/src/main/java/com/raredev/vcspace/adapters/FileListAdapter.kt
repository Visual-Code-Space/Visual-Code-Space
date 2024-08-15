package com.raredev.vcspace.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.raredev.vcspace.databinding.LayoutFileItemBinding
import com.raredev.vcspace.providers.FileIconProvider
import com.raredev.vcspace.resources.R
import java.io.File
import java.text.SimpleDateFormat

class FileListAdapter(private val listener: OnFileClickListener) :
  RecyclerView.Adapter<FileListAdapter.VH>() {

  private var files: List<File> = emptyList()

  inner class VH(internal val binding: LayoutFileItemBinding) :
    RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    return VH(LayoutFileItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
  }

  @SuppressLint("SimpleDateFormat")
  override fun onBindViewHolder(holder: VH, position: Int) {
    holder.binding.apply {
      val file = files[position]

      val iconRes: Int =
        if (file.isFile) {
          FileIconProvider.findFileIconResource(file)
        } else R.drawable.ic_folder

      icon.setImageResource(iconRes)
      name.text = file.name
      info.text =
        root.context.getString(
          R.string.file_modified_in,
          SimpleDateFormat("yy/MM/dd").format(file.lastModified())
        )

      root.setOnClickListener { listener.onFileClickListener(file) }
      root.setOnLongClickListener { listener.onFileLongClickListener(file, it) }
    }
  }

  override fun getItemCount(): Int {
    return files.size
  }

  fun submitList(newFiles: List<File>) {
    val diffResult = DiffUtil.calculateDiff(FileDiffCallback(this.files, newFiles))
    this.files = newFiles
    diffResult.dispatchUpdatesTo(this)
  }

  interface OnFileClickListener {
    fun onFileClickListener(file: File)

    fun onFileLongClickListener(file: File, view: View): Boolean
  }

  inner class FileDiffCallback(private val oldList: List<File>, private val newList: List<File>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int {
      return oldList.size
    }

    override fun getNewListSize(): Int {
      return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
      return oldList[oldItemPosition].name == newList[newItemPosition].name
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
      return oldList[oldItemPosition].path == newList[newItemPosition].path
    }
  }
}
