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

package com.teixeira.vcspace.screens.editor.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuOpen
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.FileUtils
import com.teixeira.vcspace.activities.SettingsActivity
import com.teixeira.vcspace.activities.TerminalActivity
import com.teixeira.vcspace.activities.editor.LocalDrawerState
import com.teixeira.vcspace.core.components.Tooltip
import com.teixeira.vcspace.core.components.editor.FileOptionsSheet
import com.teixeira.vcspace.core.components.file.FileExplorer
import com.teixeira.vcspace.core.settings.Settings.File.rememberShowHiddenFiles
import com.teixeira.vcspace.events.OnDeleteFileEvent
import com.teixeira.vcspace.events.OnRenameFileEvent
import com.teixeira.vcspace.extensions.open
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.resources.R.string
import com.teixeira.vcspace.utils.launchWithProgressDialog
import com.teixeira.vcspace.utils.showShortToast
import com.teixeira.vcspace.viewmodel.editor.EditorViewModel
import com.teixeira.vcspace.viewmodel.file.FileExplorerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import java.io.File

@Composable
fun EditorDrawerSheet(
  fileExplorerViewModel: FileExplorerViewModel,
  editorViewModel: EditorViewModel
) {
  val context = LocalContext.current
  val drawerState = LocalDrawerState.current
  val scope = rememberCoroutineScope()
  val selectedItem by remember { mutableIntStateOf(0) }

  val navigationRailItems = listOf(
    stringResource(string.file_explorer),
    stringResource(string.terminal),
    stringResource(string.settings)
  )
  val navRailItemIconsUnselected = listOf(
    Icons.Outlined.Folder,
    Icons.Outlined.Terminal,
    Icons.Outlined.Settings
  )
  val navRailItemIconsSelected = listOf(
    Icons.Rounded.Folder,
    Icons.Rounded.Terminal,
    Icons.Rounded.Settings
  )

  fun closeDrawer() {
    scope.launch {
      drawerState.apply {
        if (isOpen) close()
      }
    }
  }

  var selectedFile by remember { mutableStateOf<File?>(null) }

  Row(
    modifier = Modifier.fillMaxSize()
  ) {
    NavigationRail(
      modifier = Modifier.widthIn(max = 72.dp)
    ) {
      navigationRailItems.fastForEachIndexed { i, name ->
        NavigationRailItem(
          icon = {
            Icon(
              imageVector = if (selectedItem == i) navRailItemIconsSelected[i] else navRailItemIconsUnselected[i],
              contentDescription = name
            )
          },
          selected = selectedItem == i,
          onClick = {
            when (i) {
              1 -> context.open(TerminalActivity::class.java)
              2 -> context.open(SettingsActivity::class.java)
            }
          }
        )
      }
    }

    Column(
      modifier = Modifier.fillMaxSize()
    ) {
      Row {
        Text(
          text = stringResource(string.workspace),
          style = MaterialTheme.typography.headlineSmall,
          modifier = Modifier
            .padding(5.dp)
            .padding(start = 5.dp)
            .fillMaxWidth()
            .weight(1f),
          color = MaterialTheme.colorScheme.tertiary
        )

        Tooltip(stringResource(string.close_drawer)) {
          IconButton(
            onClick = { closeDrawer() },
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 0.dp)
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.MenuOpen,
              contentDescription = stringResource(string.close_drawer)
            )
          }
        }
      }

      FileExplorer(
        viewModel = fileExplorerViewModel,
        editorViewModel = editorViewModel,
        onFileClick = { closeDrawer() },
        onFileLongClick = { selectedFile = it }
      )

      var renamableFile by remember { mutableStateOf<File?>(null) }
      var deletableFile by remember { mutableStateOf<File?>(null) }

      if (selectedFile != null) {
        FileOptionsSheet(onDismissRequest = { selectedFile = null }) {
          when (it) {
            0 -> ClipboardUtils.copyText(selectedFile!!.absolutePath)
            1 -> renamableFile = selectedFile
            2 -> deletableFile = selectedFile
            else -> {}
          }
        }
      }

      if (deletableFile != null) {
        DeleteFileDialog(
          file = deletableFile!!,
          fileExplorerViewModel = fileExplorerViewModel,
          onDismissRequest = { deletableFile = null }
        )
      }

      if (renamableFile != null) {
        RenameFileDialog(
          file = renamableFile!!,
          fileExplorerViewModel = fileExplorerViewModel,
          onDismissRequest = { renamableFile = null }
        )
      }
    }
  }
}

@Composable
fun RenameFileDialog(
  file: File,
  fileExplorerViewModel: FileExplorerViewModel,
  onDismissRequest: () -> Unit
) {
  var fileName by remember { mutableStateOf(file.name) }

  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  val showHiddenFiles by rememberShowHiddenFiles()

  AlertDialog(
    onDismissRequest = onDismissRequest,
    title = { Text(stringResource(string.file_rename)) },
    text = {
      OutlinedTextField(
        value = fileName,
        onValueChange = { fileName = it },
        isError = fileName.isEmpty(),
        placeholder = { Text(stringResource(string.file_enter_name)) }
      )
    },
    confirmButton = {
      TextButton(
        onClick = {
          scope.launchWithProgressDialog(
            uiContext = context,
            configureBuilder = { builder ->
              builder.setMessage(R.string.file_renaming)
              builder.setCancelable(false)
            },
            action = { _, _ ->
              val newFile = File(file.parentFile, fileName)
              val renamed = file.renameTo(newFile)

              if (!renamed) {
                return@launchWithProgressDialog
              }

              EventBus.getDefault().post(OnRenameFileEvent(file, newFile))

              withContext(Dispatchers.Main) {
                showShortToast(context, context.getString(R.string.file_renamed))
                fileExplorerViewModel.refreshFiles(showHiddenFiles)
              }

              onDismissRequest()
            },
          )
        },
        enabled = fileName.isNotEmpty()
      ) {
        Text(stringResource(string.yes))
      }
    },
    dismissButton = {
      TextButton(onClick = onDismissRequest) {
        Text(stringResource(string.no))
      }
    }
  )
}

@Composable
fun DeleteFileDialog(
  file: File,
  fileExplorerViewModel: FileExplorerViewModel,
  onDismissRequest: () -> Unit
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  val showHiddenFiles by rememberShowHiddenFiles()

  AlertDialog(
    onDismissRequest = onDismissRequest,
    title = { Text(stringResource(string.file_delete)) },
    text = { Text(stringResource(string.file_delete_message, file.name)) },
    confirmButton = {
      TextButton(onClick = {
        scope.launchWithProgressDialog(
          uiContext = context,
          configureBuilder = { builder ->
            builder.setMessage(R.string.file_deleting)
            builder.setCancelable(false)
          },
          action = { _, _ ->
            val deleted = FileUtils.delete(file)

            if (!deleted) {
              return@launchWithProgressDialog
            }

            EventBus.getDefault().post(OnDeleteFileEvent(file))

            withContext(Dispatchers.Main) {
              showShortToast(context, context.getString(string.file_deleted))
              fileExplorerViewModel.refreshFiles(showHiddenFiles)
            }

            onDismissRequest()
          },
        )
      }) { Text(stringResource(string.yes)) }
    },
    dismissButton = {
      TextButton(onClick = onDismissRequest) {
        Text(stringResource(string.no))
      }
    }
  )
}