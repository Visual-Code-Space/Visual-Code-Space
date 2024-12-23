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

import com.teixeira.vcspace.file.File
import io.github.dingyi222666.view.treeview.AbstractTree
import io.github.dingyi222666.view.treeview.Tree
import io.github.dingyi222666.view.treeview.TreeNode
import io.github.dingyi222666.view.treeview.TreeNodeGenerator
import kotlinx.coroutines.CoroutineScope

class FileNodeGenerator(
  private val prefetchScope: CoroutineScope,
  private val rootPath: File,
  private val fileListLoader: FileListLoader
) : TreeNodeGenerator<File> {

  override fun createNode(
    parentNode: TreeNode<File>,
    currentData: File,
    tree: AbstractTree<File>
  ): TreeNode<File> {
    return TreeNode(
      data = currentData,
      depth = parentNode.depth + 1,
      name = currentData.name,
      id = tree.generateId(),
      hasChild = currentData.isDirectory && fileListLoader.getCacheFileList(currentData)
        .isNotEmpty(),
      isChild = currentData.isDirectory,
      expand = false
    )
  }

  override suspend fun fetchChildData(targetNode: TreeNode<File>): Set<File> {
    val path = targetNode.requireData()
    var files = fileListLoader.getCacheFileList(path)

    if (files.isEmpty()) {
      files = fileListLoader.loadFileList(prefetchScope, path)
    }

    return files.toSet()
  }

  override fun createRootNode(): TreeNode<File> {
    return TreeNode(
      data = rootPath,
      depth = 0,
      name = rootPath.name,
      id = Tree.ROOT_NODE_ID,
      hasChild = true,
      isChild = true,
    )
  }
}