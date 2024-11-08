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

package com.teixeira.vcspace.ui.components.git

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.UriUtils
import com.teixeira.vcspace.APP_EXTERNAL_DIR
import com.teixeira.vcspace.activities.base.LocalLifecycleScope
import com.teixeira.vcspace.app.strings
import com.teixeira.vcspace.git.GitManager
import com.teixeira.vcspace.ui.screens.file.FileExplorerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CloneRepositoryDialog(
  modifier: Modifier = Modifier,
  fileExplorerViewModel: FileExplorerViewModel,
  url: String,
  onDismissRequest: () -> Unit,
  onSuccessfulClone: () -> Unit,
  onFailedClone: (Throwable) -> Unit
) {
  val appDir = remember { APP_EXTERNAL_DIR }

  val path by fileExplorerViewModel.openedFolder.collectAsStateWithLifecycle()
  var currentPath by remember {
    mutableStateOf(
      if (path?.isFile == true) {
        path?.parentFile?.absolutePath ?: appDir
      } else if (path?.startsWith("/data") == true) {
        appDir
      } else {
        path?.absolutePath ?: appDir
      }
    )
  }

  var gitUrl by remember { mutableStateOf(url) }
  var destination by remember { mutableStateOf(currentPath) }

  var isCloning by remember { mutableStateOf(false) }
  var progress by remember { mutableIntStateOf(0) }
  var statusMessage by remember { mutableStateOf("Starting clone...") }

  val context = LocalContext.current
  val openFolder = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocumentTree()
  ) { uri ->
    if (uri != null) DocumentFile.fromTreeUri(context, uri)?.let {
      currentPath = UriUtils.uri2File(it.uri).absolutePath
    }
  }

  val scope = LocalLifecycleScope.current

  LaunchedEffect(gitUrl, currentPath) {
    if (gitUrl.isNotEmpty()) {
      var dest = "$currentPath/${gitUrl.substringAfterLast("/")}"
      if (dest.endsWith(".git")) {
        dest = dest.substringBeforeLast(".")
      }
      destination = dest
    }
  }

  AlertDialog(
    modifier = modifier,
    onDismissRequest = onDismissRequest,
    title = {
      Text("Clone Repository")
    },
    text = {
      if (isCloning) {
        Column {
          Text("$statusMessage ($progress%)")
          Spacer(modifier = Modifier.height(8.dp))
          LinearProgressIndicator(
            progress = { progress / 100f },
          )
        }
      } else {
        Column {
          OutlinedTextField(
            value = gitUrl,
            onValueChange = { gitUrl = it },
            label = { Text("Git URL") }
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Destination Path") },
            trailingIcon = {
              IconButton(onClick = {
                openFolder.launch(null)
              }) { Icon(Icons.Rounded.Folder, contentDescription = null) }
            }
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          scope.launch(Dispatchers.IO) {
            runCatching {
              isCloning = true
              GitManager.instance.clone(
                url = gitUrl,
                destination = destination,
                onUpdate = { percentDone, taskName ->
                  progress = percentDone
                  statusMessage = "Cloning: $taskName"
                }
              )
            }.onSuccess {
              isCloning = false
              onSuccessfulClone()
              onDismissRequest()
            }.onFailure(onFailedClone)
          }
        },
        enabled = gitUrl.isNotEmpty() && !isCloning && destination.isNotEmpty()
      ) {
        Text(if (isCloning) "Cloning..." else stringResource(strings.git_clone))
      }
    },
    dismissButton = {
      if (!isCloning) {
        OutlinedButton(onClick = onDismissRequest) { Text(stringResource(strings.cancel)) }
      }
    },
    properties = DialogProperties(
      dismissOnClickOutside = !isCloning,
      dismissOnBackPress = !isCloning
    )
  )
}