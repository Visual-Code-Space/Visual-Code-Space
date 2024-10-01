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

package com.teixeira.vcspace.core.components.file

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.teixeira.vcspace.providers.FileIconProvider
import com.teixeira.vcspace.resources.R
import java.io.File
import java.text.SimpleDateFormat

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("SimpleDateFormat")
@Composable
fun FileList(
  files: List<File>,
  modifier: Modifier = Modifier,
  onFileLongClick: ((File) -> Unit)? = null,
  onFileClick: (File) -> Unit,
) {
  val context = LocalContext.current

  if (files.isEmpty()) {
    Box(
      modifier = modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = stringResource(R.string.file_empty_folder)
      )
    }
  } else {
    LazyColumn(
      modifier = modifier.fillMaxWidth()
    ) {
      items(files) {
        val icon = if (it.isFile) {
          ImageVector.vectorResource(FileIconProvider.findFileIconResource(it))
        } else Icons.Rounded.Folder

        ListItem(
          headlineContent = {
            Text(
              text = it.name,
              style = MaterialTheme.typography.bodyMedium,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.offset { IntOffset(-12, 0) }
            )
          },
          supportingContent = {
            Text(
              text = context.getString(
                R.string.file_modified_in,
                SimpleDateFormat("yy/MM/dd").format(it.lastModified()),
              ),
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Light,
              modifier = Modifier.offset { IntOffset(-12, 0) }
            )
          },
          leadingContent = {
            Icon(
              imageVector = icon,
              contentDescription = it.name,
              modifier = Modifier.size(24.dp)
            )
          },
          colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
          ),
          modifier = Modifier
            .heightIn(max = 45.dp)
            .combinedClickable(
              onClick = { onFileClick(it) },
              onLongClick = { onFileLongClick?.invoke(it) }
            )
        )
      }
    }
  }
}