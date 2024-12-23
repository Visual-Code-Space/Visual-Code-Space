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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.teixeira.vcspace.activities.base.LocalLifecycleScope
import com.teixeira.vcspace.file.File
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.resources.R.string
import com.teixeira.vcspace.ui.LocalToastHostState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

@Composable
fun NewFileDialog(
  path: File,
  onDismissRequest: () -> Unit,
  onFileCreated: (File) -> Unit = {},
  onFolderCreated: (File) -> Unit = {},
) {
  var fileName by remember { mutableStateOf("") }

  val toastHostState = LocalToastHostState.current
  val lifecycleScope = LocalLifecycleScope.current
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  AlertDialog(
    onDismissRequest = onDismissRequest,
    title = { Text(stringResource(R.string.create)) },
    text = {
      OutlinedTextField(
        value = fileName,
        onValueChange = { fileName = it },
        isError = fileName.isEmpty(),
        textStyle = MaterialTheme.typography.bodyLarge,
        placeholder = { Text(stringResource(R.string.file_enter_name)) }
      )
    },
    confirmButton = {
      Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp)
      ) {
        TextButton(
          onClick = {
            try {
              if (!path.childExists(fileName)) {
                lifecycleScope.launch(Dispatchers.IO) {
                  path.createNewFile(fileName)?.let{
                    withContext(Dispatchers.Main) {
                      onFileCreated(it)
                    }
                  }
                }
              } else {
                scope.launch {
                  toastHostState.showToast(
                    message = context.getString(string.already_exists),
                    icon = Icons.Rounded.ErrorOutline
                  )
                }
              }
            } catch (ioe: IOException) {
              ioe.printStackTrace()
              scope.launch {
                toastHostState.showToast(
                  message = ioe.message ?: context.getString(string.error),
                  icon = Icons.Rounded.ErrorOutline
                )
              }
            }

            onDismissRequest()
          },
          enabled = fileName.isNotEmpty()
        ) {
          Text(stringResource(R.string.file))
        }
        TextButton(
          onClick = {
            try {
              if (!path.childExists(fileName)) {
                lifecycleScope.launch(Dispatchers.IO) {
                  path.createNewDirectory(fileName)?.let {
                    withContext(Dispatchers.Main) {
                      onFolderCreated(it)
                    }
                  }
                }
              } else {
                scope.launch {
                  toastHostState.showToast(
                    message = context.getString(string.already_exists),
                    icon = Icons.Rounded.ErrorOutline
                  )
                }
              }
            } catch (ioe: IOException) {
              ioe.printStackTrace()
              scope.launch {
                toastHostState.showToast(
                  message = ioe.message ?: context.getString(string.error),
                  icon = Icons.Rounded.ErrorOutline
                )
              }
            }
            onDismissRequest()
          },
          enabled = fileName.isNotEmpty()
        ) {
          Text(stringResource(R.string.file_folder))
        }
      }
    },
    dismissButton = {
      TextButton(onClick = onDismissRequest) {
        Text(stringResource(R.string.no))
      }
    }
  )
}