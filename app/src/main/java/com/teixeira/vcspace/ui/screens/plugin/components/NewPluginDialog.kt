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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.teixeira.vcspace.preferences.pluginsPath
import com.teixeira.vcspace.ui.ErrorMessage
import com.teixeira.vcspace.ui.InputField
import com.vcspace.plugins.Manifest
import com.vcspace.plugins.Script
import java.io.File

@Composable
fun NewPluginDialog(
  modifier: Modifier = Modifier,
  onCreate: (Manifest) -> Unit,
  onDismiss: () -> Unit
) {
  val defaultPackageName = "com.example.plugin"
  val defaultAuthorName = "Unknown"
  val defaultDescription = "No description provided"

  var name by remember { mutableStateOf("") }
  var packageName by remember { mutableStateOf(defaultPackageName) }
  var author by remember { mutableStateOf(defaultAuthorName) }
  var description by remember { mutableStateOf("") }
  var isValid by remember { mutableStateOf(false) }
  var isValidPackageName by remember { mutableStateOf(true) }

  LaunchedEffect(name, packageName) {
    val packageFile = File("$pluginsPath/$packageName")
    isValidPackageName = !packageFile.exists() && packageName.isNotBlank()
    isValid = isValidPackageName && name.isNotBlank()
  }

  AlertDialog(
    modifier = modifier,
    onDismissRequest = onDismiss,
    title = { Text("New Plugin") },
    text = {
      Column {
        InputField(
          label = "Name",
          value = name,
          onValueChange = { name = it }
        )

        InputField(
          label = "Package Name",
          value = packageName,
          placeholder = { Text(defaultPackageName) },
          onValueChange = { packageName = it },
          isError = !isValidPackageName
        )

        (if (!isValidPackageName) "Package name already exists" else null)?.let {
          ErrorMessage(message = it)
        }

        InputField(
          label = "Author",
          value = author,
          placeholder = { Text(defaultAuthorName) },
          onValueChange = { author = it }
        )

        InputField(
          label = "Description",
          value = description,
          placeholder = { Text(defaultDescription) },
          onValueChange = { description = it },
          singleLine = false,
          maxLines = 3
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (isValid) {
            val newManifest = Manifest(
              name = name,
              packageName = packageName,
              author = author,
              description = description.ifEmpty { defaultDescription },
              scripts = arrayOf(
                Script(
                  name = "${name.lowercase().replace(" ", "_")}.java",
                  entryPoint = "main"
                )
              )
            )
            onCreate(newManifest)
          }
        },
        enabled = isValid
      ) {
        Text("Create")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}