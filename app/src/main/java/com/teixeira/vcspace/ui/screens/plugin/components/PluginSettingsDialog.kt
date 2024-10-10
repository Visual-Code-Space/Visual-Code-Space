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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.teixeira.vcspace.extensions.toFile
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.ui.ErrorMessage
import java.io.File

@Composable
fun PluginSettingsDialog(
  modifier: Modifier = Modifier,
  settings: PluginSettings,
  onSettingsChanged: ((PluginSettings) -> Unit)? = null,
  onDismiss: () -> Unit
) {
  var path by remember { mutableStateOf(settings.pluginPath) }
  var errorMessage: String? by remember { mutableStateOf(null) }
  var showCreateDirDialog by remember { mutableStateOf(false) }

  fun checkError(path: String) {
    val file = File(path)
    errorMessage = when {
      !file.isAbsolute -> "Invalid folder path"
      !file.isDirectory && file.exists() -> "Path is not a directory"
      else -> null
    }
  }

  LaunchedEffect(path) { checkError(path) }

  AlertDialog(
    modifier = modifier,
    onDismissRequest = onDismiss,
    title = { Text(stringResource(R.string.plugin_settings)) },
    text = {
      Column {
        OutlinedTextField(
          value = path,
          onValueChange = { path = it },
          label = { Text(stringResource(R.string.plugins_path)) },
          isError = errorMessage != null,
          keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = false,
            imeAction = ImeAction.Done
          )
        )

        errorMessage?.let { ErrorMessage(message = it) }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (path.toFile().exists()) {
            onSettingsChanged?.invoke(settings.copy(pluginPath = path))
            onDismiss()
          } else showCreateDirDialog = true
        },
        enabled = errorMessage == null
      ) {
        Text(stringResource(R.string.confirm))
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text(stringResource(R.string.cancel))
      }
    }
  )

  if (showCreateDirDialog) {
    ConfirmCreateDirectoryDialog(
      onCreate = {
        File(path).mkdirs()
        showCreateDirDialog = false
        onSettingsChanged?.invoke(settings.copy(pluginPath = path))
        onDismiss()
      },
      onCancel = { showCreateDirDialog = false }
    )
  }
}

@Composable
fun ConfirmCreateDirectoryDialog(
  onCreate: () -> Unit,
  onCancel: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onCancel,
    title = { Text(stringResource(R.string.directory_not_found)) },
    text = { Text(stringResource(R.string.the_directory_does_not_exist_would_you_like_to_create_it)) },
    confirmButton = {
      Button(onClick = onCreate) { Text(stringResource(R.string.create)) }
    },
    dismissButton = {
      OutlinedButton(onClick = onCancel) { Text(stringResource(R.string.cancel)) }
    }
  )
}
