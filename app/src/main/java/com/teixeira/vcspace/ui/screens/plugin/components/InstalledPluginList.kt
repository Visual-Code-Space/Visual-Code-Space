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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.teixeira.vcspace.plugins.internal.PluginInfo
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.ui.LocalToastHostState
import com.teixeira.vcspace.ui.screens.plugin.PluginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun InstalledPluginList(
    modifier: Modifier = Modifier,
    viewModel: PluginViewModel,
    listState: LazyListState,
    scope: CoroutineScope,
    onUpdateClick: (PluginInfo) -> Unit
) {
    val plugins by viewModel.installedPlugins.collectAsStateWithLifecycle()

    var selectedPlugin by remember { mutableStateOf<PluginInfo?>(null) }
    val context = LocalContext.current
    val toastHostState = LocalToastHostState.current

    if (plugins.isEmpty()) {
        NoPlugins()
    } else {
        LazyColumn(
            state = listState,
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 5.dp, horizontal = 5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(plugins, key = { "${it.name}${it.pluginFileName}${it.version}" }) { plugin ->
                PluginListItem(
                    modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null),
                    pluginInfo = plugin,
                    onLongClick = { selectedPlugin = it },
                    onClick = {

                    },
                    onEnabledOrDisabledCallback = {
                        scope.launch {
                            toastHostState.showToast(
                                message = context.getString(R.string.restart_application),
                                icon = Icons.Rounded.Refresh
                            )
                        }
                    }
                )
            }
        }
    }

    selectedPlugin?.let {
        PluginActionsSheet(
            pluginInfo = it,
            viewModel = viewModel,
            scope = scope,
            onDismissSheet = { selectedPlugin = null },
            onUpdateClick = { onUpdateClick(it) }
        )
    }
}

@Composable
fun NoPlugins(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.no_plugins_found),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

