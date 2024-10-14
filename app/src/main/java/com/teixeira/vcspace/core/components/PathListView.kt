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

package com.teixeira.vcspace.core.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

@Composable
fun PathListView(
  path: File?,
  modifier: Modifier = Modifier,
  onPathClick: (File) -> Unit
) {
  val paths = remember { mutableStateListOf<File>() }
  val listState = rememberLazyListState()

  LaunchedEffect(path) {
    val dir = if (path?.isDirectory == true) path else path?.parentFile

    if (dir?.isDirectory == true) {
      paths.clear()

      var temp: File? = dir
      while (temp != null) {
        if (temp.absolutePath.equals("/storage/emulated") || temp.absolutePath.equals("/")) {
          break
        }
        paths.add(temp)
        temp = temp.parentFile
      }
      paths.reverse()

      listState.animateScrollToItem(paths.size - 1)
    }
  }

  LazyRow(
    modifier = modifier,
    state = listState
  ) {
    itemsIndexed(paths) { index, file ->
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = when (file.absolutePath) {
            "/storage/emulated/0" -> "Device storage"
            else -> file.name
          },
          modifier = Modifier
            .height(25.dp)
            .widthIn(max = 150.dp)
            .clip(MaterialTheme.shapes.small)
            .clickable { onPathClick(file) }
            .padding(horizontal = 6.dp, vertical = 3.dp),
          maxLines = 1,
          fontFamily = FontFamily.SansSerif,
          fontWeight = FontWeight.Medium,
          style = MaterialTheme.typography.bodySmall,
          fontSize = 14.sp,
          color = if (index == paths.size - 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
          overflow = TextOverflow.Ellipsis
        )

        AnimatedVisibility(
          visible = index != paths.size - 1
        ) {
          Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            modifier = Modifier
              .height(25.dp)
              .width(16.dp)
          )
        }
      }
    }
  }
}