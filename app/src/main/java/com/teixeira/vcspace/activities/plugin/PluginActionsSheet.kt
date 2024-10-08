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

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.teixeira.vcspace.extensions.toFile
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.ui.LoadingDialog
import com.teixeira.vcspace.ui.LocalToastHostState
import com.teixeira.vcspace.viewmodel.PluginViewModel
import com.vcspace.plugins.Plugin
import com.vcspace.plugins.internal.PluginManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PluginActionsSheet(
  modifier: Modifier = Modifier,
  plugin: Plugin,
  viewModel: PluginViewModel,
  scope: CoroutineScope,
  onDismissSheet: () -> Unit,
) {
  var showDeleteDialog by remember { mutableStateOf(false) }
  var showUploadDialog by remember { mutableStateOf(false) }

  val toastHostState = LocalToastHostState.current

  ModalBottomSheet(
    modifier = modifier,
    onDismissRequest = onDismissSheet
  ) {
    LazyColumn {
      item {
        ElevatedCard(
          modifier = Modifier.padding(5.dp),
          onClick = { showDeleteDialog = true }
        ) {
          ListItem(
            headlineContent = { Text("Delete Plugin") },
            leadingContent = { Icon(Icons.Rounded.Delete, contentDescription = null) }
          )
        }
      }
      item {
        ElevatedCard(
          modifier = Modifier.padding(5.dp),
          onClick = {
            showUploadDialog = true

            PluginManager.uploadPlugin(plugin) { success, errorMessage ->
              showUploadDialog = false
              onDismissSheet()

              if (success) {
                viewModel.loadPlugins()
                scope.launch {
                  toastHostState.showToast(
                    message = "Plugin uploaded successfully",
                    icon = Icons.Outlined.CheckCircle
                  )
                }
              } else {
                scope.launch {
                  toastHostState.showToast(
                    message = errorMessage.toString(),
                    icon = Icons.Outlined.Info
                  )
                }
              }
            }
          }
        ) {
          ListItem(
            headlineContent = { Text("Upload to server") },
            leadingContent = {
              Icon(
                ImageVector.vectorResource(R.drawable.ic_cloud_upload),
                contentDescription = null
              )
            }
          )
        }
      }
    }
  }

  if (showDeleteDialog) {
    DeletePluginDialog(
      onConfirm = {
        showDeleteDialog = false
        plugin.fullPath.toFile().deleteRecursively()
        onDismissSheet()
        viewModel.loadInstalledPlugins()
        scope.launch {
          toastHostState.showToast(
            message = "Plugin deleted successfully",
            icon = Icons.Outlined.CheckCircle
          )
        }
      },
      onDismiss = { showDeleteDialog = false }
    )
  }

  if (showUploadDialog) {
    LoadingDialog(message = "Uploading")
  }
}