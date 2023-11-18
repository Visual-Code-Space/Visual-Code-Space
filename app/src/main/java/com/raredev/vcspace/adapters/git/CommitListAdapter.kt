package com.raredev.vcspace.adapters.git

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.raredev.vcspace.databinding.LayoutCommitItemBinding
import org.eclipse.jgit.revwalk.RevCommit

class CommitListAdapter(private val commits: List<RevCommit>) :
    RecyclerView.Adapter<CommitListAdapter.VH>() {

  inner class VH(binding: LayoutCommitItemBinding) : RecyclerView.ViewHolder(binding.root) {
    val commitMessage: TextView = binding.commitMessage
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    val binding =
        LayoutCommitItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return VH(binding)
  }

  override fun onBindViewHolder(holder: VH, position: Int) {
    holder.commitMessage.text = commits[position].shortMessage
  }

  override fun getItemCount(): Int {
    return commits.size
  }
}
