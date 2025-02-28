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

package com.teixeira.vcspace.compose.ui.filetree

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@SuppressLint("MaterialDesignInsteadOrbitDesign")
@Composable
fun FileTreeView(
    rootNode: FileTreeNode,
    modifier: Modifier = Modifier,
    onFileClick: (FileTreeNode) -> Unit,
    onFileLongClick: (FileTreeNode) -> Unit = {},
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(rememberScrollState())
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    FileTreeNodeItem(
                        node = rootNode,
                        depth = 0,
                        onFileClick = onFileClick,
                        onFileLongClick = onFileLongClick
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("MaterialDesignInsteadOrbitDesign")
@Composable
private fun FileTreeNodeItem(
    node: FileTreeNode,
    depth: Int,
    onFileClick: (FileTreeNode) -> Unit,
    onFileLongClick: (FileTreeNode) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val hasChildren = node.isDirectory && node.children.isNotEmpty()
    val horizontalPadding = (depth * 16).dp

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        if (node.isDirectory) {
                            isExpanded = !isExpanded
                        } else {
                            onFileClick(node)
                        }
                    },
                    onLongClick = {
                        onFileLongClick(node)
                    }
                )
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(horizontalPadding))

            // Show arrow only for directories with children
            if (hasChildren) {
                val rotationDegree by animateFloatAsState(
                    targetValue = if (isExpanded) 0f else -90f,
                    label = "Arrow rotation animation"
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Expand/Collapse",
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotationDegree),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            } else {
                Spacer(modifier = Modifier.width(24.dp))
            }

            Icon(
                imageVector = if (node.isDirectory) {
                    if (isExpanded) Icons.Default.FolderOpen else Icons.Default.Folder
                } else getIconForFile(node),
                contentDescription = if (node.isDirectory) "Folder" else "File",
                tint = if (node.isDirectory) Color(0xFFFFCA28) else MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.8f
                ),
                modifier = Modifier.size(20.dp)
            )

//            Image(
//                bitmap = rememberSvgAssetImageBitmap("files/icons/3d.svg"),
//                contentDescription = null,
//                modifier = Modifier.size(20.dp)
//            )

            Spacer(modifier = Modifier.width(8.dp))

            // File or folder name
            Text(
                text = node.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = 16.dp)
            )
        }

        // Show children if expanded
        AnimatedVisibility(visible = isExpanded && hasChildren) {
            Column {
                node.children.forEach { childNode ->
                    FileTreeNodeItem(
                        node = childNode,
                        depth = depth + 1,
                        onFileClick = onFileClick,
                        onFileLongClick = onFileLongClick
                    )
                }
            }
        }
    }
}
