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

package com.teixeira.vcspace.ui.filetree

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.isInvisible
import androidx.core.view.updateLayoutParams
import androidx.core.widget.TextViewCompat
import com.teixeira.vcspace.R
import com.teixeira.vcspace.app.dp
import com.teixeira.vcspace.app.drawables
import com.teixeira.vcspace.databinding.LayoutFileItemDirBinding
import com.teixeira.vcspace.databinding.LayoutFileItemFileBinding
import com.teixeira.vcspace.databinding.LayoutFileTreeBinding
import com.teixeira.vcspace.events.OnCreateFileEvent
import com.teixeira.vcspace.events.OnCreateFolderEvent
import com.teixeira.vcspace.events.OnDeleteFileEvent
import com.teixeira.vcspace.events.OnRefreshFolderEvent
import com.teixeira.vcspace.file.File
import com.teixeira.vcspace.providers.FileIconProvider
import io.github.dingyi222666.view.treeview.TreeNode
import io.github.dingyi222666.view.treeview.TreeNodeEventListener
import io.github.dingyi222666.view.treeview.TreeView
import io.github.dingyi222666.view.treeview.TreeViewBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FileViewBinder(
  private val fileTreeBinding: LayoutFileTreeBinding,
  private val fileListLoader: FileListLoader,
  private val onFileLongClick: (File) -> Unit = {},
  private val onFileClick: (File) -> Unit = {},
  private val onSurfaceColor: Color
) : TreeViewBinder<File>(), TreeNodeEventListener<File> {

  override fun bindView(
    holder: TreeView.ViewHolder,
    node: TreeNode<File>,
    listener: TreeNodeEventListener<File>
  ) {
    if (node.isChild) {
      applyDir(holder, node)
    } else {
      applyFile(holder, node)
    }

    val itemView = holder.itemView.findViewById<Space>(R.id.space)

    itemView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
      width = node.depth * 22.dp
    }
  }

  override fun createView(parent: ViewGroup, viewType: Int): View {
    if (!EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().register(this)
    }

    val layoutInflater = LayoutInflater.from(parent.context)
    return if (viewType == 1) {
      LayoutFileItemDirBinding.inflate(layoutInflater, parent, false).root
    } else {
      LayoutFileItemFileBinding.inflate(layoutInflater, parent, false).root
    }
  }

  override fun getItemViewType(node: TreeNode<File>): Int {
    if (node.isChild) {
      return 1
    }
    return 0
  }

  private fun applyFile(holder: TreeView.ViewHolder, node: TreeNode<File>) {
    val binding = LayoutFileItemFileBinding.bind(holder.itemView)

    val icon = AppCompatResources.getDrawable(
      /* context = */ binding.root.context,
      /* resId = */ FileIconProvider.findFileIconResource(node.requireData())
    )!!
    icon.setBounds(0, 0, 16.dp, 16.dp)
    icon.setTint(onSurfaceColor.toArgb())

    binding.tvName.apply {
      text = node.name.toString()
      setCompoundDrawables(
        /* left = */ icon,
        /* top = */ null,
        /* right = */ null,
        /* bottom = */ null
      )
      TextViewCompat.setCompoundDrawableTintList(this, ColorStateList.valueOf(onSurfaceColor.toArgb()))
      setTextColor(onSurfaceColor.toArgb())
    }
  }

  private fun applyDir(holder: TreeView.ViewHolder, node: TreeNode<File>) {
    val binding = LayoutFileItemDirBinding.bind(holder.itemView)
    val icon = AppCompatResources.getDrawable(
      /* context = */ binding.root.context,
      /* resId = */ drawables.ic_folder
    )!!
    icon.setBounds(0, 0, 16.dp, 16.dp)
    icon.setTint(onSurfaceColor.toArgb())

    binding.tvName.apply {
      text = node.name.toString()
      setCompoundDrawables(
        /* left = */ icon,
        /* top = */ null,
        /* right = */ null,
        /* bottom = */ null
      )
      TextViewCompat.setCompoundDrawableTintList(this, ColorStateList.valueOf(onSurfaceColor.toArgb()))
      setTextColor(onSurfaceColor.toArgb())
    }

    binding
      .ivArrow
      .animate()
      .rotation(if (node.expand) 90f else 0f)
      .setDuration(200)
      .start()

    binding.ivArrow.imageTintList = ColorStateList.valueOf(onSurfaceColor.toArgb())
  }

  override fun onClick(node: TreeNode<File>, holder: TreeView.ViewHolder) {
    if (node.isChild) {
      applyDir(holder, node)
    } else {
      onFileClick(node.requireData())
    }
  }

  override fun onLongClick(node: TreeNode<File>, holder: TreeView.ViewHolder): Boolean {
    onFileLongClick(node.requireData())
    return true
  }

  override fun onRefresh(status: Boolean) {
    fileTreeBinding.progress.isInvisible = !status
  }

  override fun onToggle(
    node: TreeNode<File>,
    isExpand: Boolean,
    holder: TreeView.ViewHolder
  ) {
    applyDir(holder, node)
  }

  @Subscribe(threadMode = ThreadMode.ASYNC)
  fun onRefreshFolderEvent(event: OnRefreshFolderEvent) {
    CoroutineScope(Dispatchers.Main).launch {
      fileListLoader.removeFileInCache(event.openedFolder)
      fileTreeBinding.treeview.refresh(withExpandable = true)
    }
  }

  @Subscribe(threadMode = ThreadMode.ASYNC)
  fun onDeleteFileEvent(event: OnDeleteFileEvent) {
    CoroutineScope(Dispatchers.Main).launch {
      fileListLoader.removeFileInCache(event.openedFolder)
      fileTreeBinding.treeview.refresh(withExpandable = true)
    }
  }

  @Subscribe(threadMode = ThreadMode.ASYNC)
  fun onCreateFileEvent(event: OnCreateFileEvent) {
    CoroutineScope(Dispatchers.Main).launch {
      fileListLoader.removeFileInCache(event.openedFolder)
      fileTreeBinding.treeview.refresh(withExpandable = true)
    }
  }

  @Subscribe(threadMode = ThreadMode.ASYNC)
  fun onCreateFolderEvent(event: OnCreateFolderEvent) {
    CoroutineScope(Dispatchers.Main).launch {
      fileListLoader.removeFileInCache(event.openedFolder)
      fileTreeBinding.treeview.refresh(withExpandable = true)
    }
  }
}