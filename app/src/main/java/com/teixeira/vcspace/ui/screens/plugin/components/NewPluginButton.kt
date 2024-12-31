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

package com.teixeira.vcspace.ui.screens.plugin.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.ui.icons.Import

@Composable
fun NewPluginButton(
  modifier: Modifier = Modifier,
  expanded: Boolean,
  onCreatePlugin: () -> Unit,
  onImportPlugin: () -> Unit
) {
  var showNewPluginSheet by remember { mutableStateOf(false) }

  ExtendedFloatingActionButton(
    onClick = { showNewPluginSheet = true },
    text = { Text(stringResource(R.string.new_plugin)) },
    icon = { Icon(Icons.Rounded.Add, contentDescription = "add plugin") },
    expanded = expanded,
    modifier = modifier
  )

  if (showNewPluginSheet) {
    NewPluginSheet(
      onDismissRequest = { showNewPluginSheet = false },
      onCreatePlugin = onCreatePlugin,
      onImportPlugin = onImportPlugin
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewPluginSheet(
  onDismissRequest: () -> Unit,
  onCreatePlugin: () -> Unit,
  onImportPlugin: () -> Unit,
  modifier: Modifier = Modifier,
) {
  ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    modifier = modifier
  ) {
    LazyColumn {
      item {
        Card(
          onClick = {
            onCreatePlugin()
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
                text = "Create new plugin"
              )
            },
            leadingContent = {
              Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = null
              )
            }
          )
        }
      }

      item {
        Card(
          onClick = {
            onImportPlugin()
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
                text = "Import plugin"
              )
            },
            leadingContent = {
              Icon(
                imageVector = Import,
                contentDescription = null
              )
            }
          )
        }
      }
    }
  }
}
