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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.teixeira.vcspace.extensions.formatSize
import com.teixeira.vcspace.github.Content
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.ui.LocalToastHostState
import com.teixeira.vcspace.ui.screens.plugin.PluginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExplorePluginList(
  modifier: Modifier = Modifier,
  viewModel: PluginViewModel,
  scope: CoroutineScope
) {
  val pluginState by viewModel.pluginState.collectAsStateWithLifecycle()

  val isLoading = pluginState.isLoading
  val plugins = pluginState.plugins

  val toastHostState = LocalToastHostState.current

  var clickedPlugin by remember { mutableStateOf<Content?>(null) }

  if (isLoading) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      CircularProgressIndicator()
    }
  } else if (plugins.isEmpty()) {
    NothingToShowHere()
  } else {
    LazyColumn(
      modifier = modifier.fillMaxSize(),
      contentPadding = PaddingValues(vertical = 5.dp, horizontal = 5.dp),
      verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
      items(plugins) {
        ElevatedCard(
          modifier = Modifier
            .clip(CardDefaults.elevatedShape)
            .combinedClickable(
              onClick = {
                clickedPlugin = it
              },
              onLongClick = {
                scope.launch {
                  toastHostState.showToast("Long click not implemented yet")
                }
              }
            )
        ) {
          ListItem(
            headlineContent = { Text(it.name) },
            supportingContent = { Text(it.size.toLong().formatSize()) },
            trailingContent = {

            },
            leadingContent = {
              Icon(
                painter = painterResource(R.drawable.ic_plugin),
                contentDescription = null
              )
            },
            colors = ListItemDefaults.colors(
              containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
          )
        }
      }
    }
  }

  clickedPlugin?.let {
    ConfirmPluginDownload(
      plugin = it,
      onConfirm = {
        viewModel.downloadPlugin(
          plugin = it,
          onSuccess = { plugin ->
            clickedPlugin = null

            scope.launch {
              toastHostState.showToast("Plugin ${plugin.manifest.name} downloaded successfully")
            }
          },
          onFailure = {
            clickedPlugin = null

            scope.launch {
              toastHostState.showToast("Failed to download plugin: ${it.message}")
            }
          }
        )
      },
      onDismiss = { clickedPlugin = null }
    )
  }
}

@Composable
fun NothingToShowHere(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = "Nothing to show here",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onBackground
    )
  }
}

@Composable
fun ConfirmPluginDownload(
  modifier: Modifier = Modifier,
  plugin: Content,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    modifier = modifier,
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "Download Plugin"
      )
    },
    text = {
      Text(
        text = "Do you want to download ${plugin.name.substringBefore(" -")}?"
      )
    },
    confirmButton = {
      TextButton(onClick = onConfirm) {
        Text(text = "Yes")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(text = "No")
      }
    }
  )
}