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

package com.teixeira.vcspace.ui.screens.editor.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.teixeira.vcspace.core.EventManager
import com.teixeira.vcspace.events.OnRenameFileEvent
import com.teixeira.vcspace.file.File
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.utils.launchWithProgressDialog
import com.teixeira.vcspace.utils.showShortToast
import com.vcspace.plugins.event.FileRenameEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus

@Composable
fun RenameFileDialog(
    file: File,
    openedFolder: File,
    onDismissRequest: () -> Unit
) {
    var fileName by remember { mutableStateOf(file.name) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.file_rename)) },
        text = {
            OutlinedTextField(
                value = fileName,
                onValueChange = { fileName = it },
                isError = fileName.isEmpty(),
                placeholder = { Text(stringResource(R.string.file_enter_name)) }
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
                            val renamedFile =
                                file.renameTo(fileName) ?: return@launchWithProgressDialog

                            EventBus.getDefault()
                                .post(OnRenameFileEvent(file, renamedFile, openedFolder))
                            EventManager.instance.postEvent(FileRenameEvent(java.io.File(file.absolutePath), java.io.File(renamedFile.absolutePath)))

                            withContext(Dispatchers.Main) {
                                showShortToast(context, context.getString(R.string.file_renamed))
                                // refresh
                            }

                            onDismissRequest()
                        },
                    )
                },
                enabled = fileName.isNotEmpty()
            ) {
                Text(stringResource(R.string.yes))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.no))
            }
        }
    )
}
