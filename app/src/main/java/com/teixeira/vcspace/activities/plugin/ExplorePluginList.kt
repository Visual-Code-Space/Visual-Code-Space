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

package com.teixeira.vcspace.activities.plugin

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.teixeira.vcspace.extensions.formatSize
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.ui.LoadingDialog
import com.teixeira.vcspace.ui.LocalToastHostState
import com.teixeira.vcspace.viewmodel.PluginViewModel
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

  if (isLoading) {
    LoadingDialog(message = "Loading")
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
                scope.launch {
                  toastHostState.showToast("Soon to be implemented")
                }
              },
              onLongClick = {

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