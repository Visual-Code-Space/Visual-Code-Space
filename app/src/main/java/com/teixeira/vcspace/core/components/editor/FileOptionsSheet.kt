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

package com.teixeira.vcspace.core.components.editor

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.DriveFileRenameOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.teixeira.vcspace.resources.R.string

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileOptionsSheet(
  modifier: Modifier = Modifier,
  onDismissRequest: () -> Unit,
  onItemClick: (Int) -> Unit = {}
) {
  ModalBottomSheet(
    modifier = modifier,
    onDismissRequest = onDismissRequest
  ) {
    repeat(3) {
      Card(
        onClick = {
          onItemClick(it)
          onDismissRequest()
        },
        colors = CardDefaults.cardColors(
          containerColor = Color.Transparent
        ),
        modifier = Modifier.padding(vertical = 3.dp, horizontal = 5.dp)
      ) {
        ListItem(
          colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
          ),
          headlineContent = {
            Text(
              when (it) {
                0 -> stringResource(string.file_copy_path)
                1 -> stringResource(string.file_rename)
                2 -> stringResource(string.file_delete)
                else -> ""
              }
            )
          },
          leadingContent = {
            Icon(
              when (it) {
                0 -> Icons.Rounded.ContentCopy
                1 -> Icons.Rounded.DriveFileRenameOutline
                2 -> Icons.Rounded.DeleteForever
                else -> Icons.Default.EmojiEmotions
              },
              contentDescription = null
            )
          }
        )
      }
    }
  }
}