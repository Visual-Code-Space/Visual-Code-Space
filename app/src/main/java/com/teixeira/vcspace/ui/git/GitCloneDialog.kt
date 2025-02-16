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

package com.teixeira.vcspace.ui.git

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.window.DialogProperties
import androidx.documentfile.provider.DocumentFile
import com.blankj.utilcode.util.UriUtils
import com.teixeira.vcspace.APP_EXTERNAL_DIR
import com.teixeira.vcspace.app.strings
import com.teixeira.vcspace.core.components.common.DialogButton
import com.teixeira.vcspace.extensions.toFile
import com.teixeira.vcspace.git.VCSGit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun GitCloneDialog(
    remoteUrl: String,
    initialFolder: File?,
    onDismissRequest: () -> Unit,
    onCloneSuccess: (File) -> Unit,
    onCloneFailure: (Throwable) -> Unit,
    modifier: Modifier = Modifier,
) {
    var localPath by remember {
        mutableStateOf(
            if (initialFolder?.isFile == true) {
                initialFolder.parentFile?.absolutePath ?: APP_EXTERNAL_DIR
            } else if (initialFolder?.startsWith("/data") == true) {
                APP_EXTERNAL_DIR
            } else {
                initialFolder?.absolutePath ?: APP_EXTERNAL_DIR
            }
        )
    }

    var gitRemoteUrl by remember { mutableStateOf(remoteUrl) }
    var localClonePath by remember { mutableStateOf(localPath) }
    var isCloneInProgress by remember { mutableStateOf(false) }
    var cloneProgress by remember { mutableIntStateOf(0) }
    var cloneStatusMessage by remember { mutableStateOf("Starting clone...") }

    val context = LocalContext.current
    val pickCloneDestination = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri != null) DocumentFile.fromTreeUri(context, uri)?.let {
            localPath = UriUtils.uri2File(it.uri).absolutePath
        }
    }

    LaunchedEffect(key1 = gitRemoteUrl, key2 = localPath) {
        if (gitRemoteUrl.isNotEmpty()) {
            var fullClonePath = "$localPath/${gitRemoteUrl.substringAfterLast("/")}"
            if (fullClonePath.endsWith(".git")) {
                fullClonePath = fullClonePath.substringBeforeLast(".")
            }
            localClonePath = fullClonePath
        }
    }

    val ioScope = rememberCoroutineScope { Dispatchers.IO }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(strings.git_clone_repo))
        },
        text = {
            if (isCloneInProgress) {
                Cloning(progress = cloneProgress, message = cloneStatusMessage)
            } else {
                Column {
                    UrlTextField(url = gitRemoteUrl, onUrlChange = { gitRemoteUrl = it })
                    Spacer(modifier = Modifier.height(8.dp))
                    DestinationTextField(
                        destination = localClonePath,
                        onDestinationChange = { localClonePath = it },
                        onPickDestinationClick = { pickCloneDestination.launch(null) }
                    )
                }
            }
        },
        confirmButton = {
            DialogButton(
                text = stringResource(strings.git_clone),
                enabled = !isCloneInProgress && gitRemoteUrl.isNotEmpty() && localClonePath.isNotEmpty(),
                onClick = {
                    ioScope.launch {
                        isCloneInProgress = true

                        runCatching {
                            VCSGit.instance.clone(
                                url = gitRemoteUrl,
                                destination = localClonePath,
                                onUpdate = { progressPercentage, progressStatus ->
                                    cloneProgress = progressPercentage
                                    cloneStatusMessage = progressStatus
                                }
                            )
                        }.onSuccess {
                            isCloneInProgress = false
                            onCloneSuccess(localClonePath.toFile())
                            onDismissRequest()
                        }.onFailure(onCloneFailure)
                    }
                }
            )
        },
        dismissButton = {
            if (isCloneInProgress.not()) {
                DialogButton(text = stringResource(strings.cancel), onClick = onDismissRequest)
            }
        },
        properties = DialogProperties(
            dismissOnClickOutside = !isCloneInProgress,
            dismissOnBackPress = !isCloneInProgress
        )
    )
}

@Composable
private fun Cloning(
    progress: Int,
    message: String
) {
    Column {
        Text(text = "$message ($progress%)")
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(progress = { progress / 100f })
    }
}

@Composable
private fun UrlTextField(
    url: String,
    onUrlChange: (String) -> Unit
) {
    OutlinedTextField(
        value = url,
        onValueChange = onUrlChange,
        label = {
            Text(text = stringResource(strings.git_url))
        }
    )
}

@Composable
private fun DestinationTextField(
    destination: String,
    onDestinationChange: (String) -> Unit,
    onPickDestinationClick: () -> Unit
) {
    OutlinedTextField(
        value = destination,
        onValueChange = onDestinationChange,
        label = {
            Text(text = stringResource(strings.git_destination_path))
        },
        trailingIcon = {
            IconButton(onClick = onPickDestinationClick) {
                Icon(
                    Icons.Rounded.Folder,
                    contentDescription = null
                )
            }
        }
    )
}
