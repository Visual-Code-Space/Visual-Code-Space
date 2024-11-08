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

package com.teixeira.vcspace.ui.screens.editor.components.drawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.teixeira.vcspace.core.components.Tooltip
import com.teixeira.vcspace.resources.R

@Composable
fun Heading(
  modifier: Modifier = Modifier,
  title: String,
  onCloseDrawerRequest: () -> Unit
) {
  Row(modifier = modifier) {
    Column(
      modifier = Modifier
        .padding(5.dp)
        .padding(start = 5.dp, bottom = 0.dp)
        .fillMaxWidth()
        .weight(1f),
    ) {
      Text(
        text = stringResource(R.string.workspace),
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary
      )
      Spacer(Modifier.height(2.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.secondary
      )
    }

    Tooltip(stringResource(R.string.close_drawer)) {
      IconButton(
        onClick = onCloseDrawerRequest,
        modifier = Modifier.padding(horizontal = 5.dp, vertical = 0.dp)
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Rounded.MenuOpen,
          contentDescription = stringResource(R.string.close_drawer)
        )
      }
    }
  }
}