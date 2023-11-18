package com.raredev.vcspace.adapters.git

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.raredev.vcspace.databinding.LayoutGitChangedFileItemBinding
import com.raredev.vcspace.models.git.ChangedFileItem

class ChangedFileAdapter(commits: Set<String>) : RecyclerView.Adapter<ChangedFileAdapter.VH>() {

  private val commitsList: List<ChangedFileItem> = commits.map { ChangedFileItem(it, "") }

  inner class VH(binding: LayoutGitChangedFileItemBinding) : RecyclerView.ViewHolder(binding.root) {
    val fileName: TextView = binding.fileName
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    val binding =
        LayoutGitChangedFileItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return VH(binding)
  }

  override fun onBindViewHolder(holder: VH, position: Int) {
    holder.fileName.text = commitsList[position].fileName
  }

  override fun getItemCount(): Int {
    return commitsList.size
  }
}
