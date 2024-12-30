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

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.UriUtils
import com.teixeira.vcspace.APP_EXTERNAL_DIR
import com.teixeira.vcspace.app.strings
import com.teixeira.vcspace.extensions.toFile
import com.teixeira.vcspace.file.DocumentFileWrapper
import com.teixeira.vcspace.file.wrapFile
import com.teixeira.vcspace.plugins.internal.PluginInfo
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.ui.rememberSheetState
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPluginSheet(
  onCreate: (PluginInfo, directory: String) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val defaultMainClass = "MyPlugin"
  val defaultVersion = "1.0.0"
  val defaultPackageName = "com.example.myplugin"
  val defaultAuthorName = "Unknown"
  val defaultDescription = "A sample plugin for VCSpace"

  var name by remember { mutableStateOf(defaultMainClass) }
  var packageName by remember { mutableStateOf(defaultPackageName) }
  var author by remember { mutableStateOf(defaultAuthorName) }
  var description by remember { mutableStateOf(defaultDescription) }
  var mainClass by remember { mutableStateOf(defaultMainClass) }
  var version by remember { mutableStateOf(defaultVersion) }
  var directory by remember { mutableStateOf("$APP_EXTERNAL_DIR/plugins") }
  var id by remember { mutableStateOf(name.lowercase()) }
  var website by remember { mutableStateOf("") }
  var license by remember { mutableStateOf("") }
  var isValid by remember { mutableStateOf(false) }
  var isValidPackageName by remember { mutableStateOf(true) }
  var isValidClassName by remember { mutableStateOf(true) }

  LaunchedEffect(name, packageName, id, mainClass) {
    val packageFile = File("$directory/$name")
    isValidPackageName = packageName.isNotBlank()
    isValidClassName = mainClass.isNotBlank() && !mainClass.contains(" ")
    isValid = isValidPackageName && name.isNotBlank() && isValidClassName && !packageFile.exists()
  }

  val context = LocalContext.current
  val openFolder = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocumentTree()
  ) { uri ->
    if (uri != null) DocumentFile.fromTreeUri(context, uri)?.let {
      val file = if (DocumentFileWrapper.shouldWrap(uri)) {
        DocumentFileWrapper(it)
      } else {
        UriUtils.uri2File(it.uri).wrapFile()
      }
      directory = file.absolutePath
    }
  }

  val sheetState = rememberSheetState(
    initialValue = SheetValue.Expanded,
    skipHiddenState = false,
    skipPartiallyExpanded = true
  )

  ModalBottomSheet(
    sheetState = sheetState,
    onDismissRequest = onDismiss,
    dragHandle = {},
    modifier = modifier.imePadding()
  ) {
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text("Create Plugin") },
          navigationIcon = {
            IconButton(onClick = { onDismiss() }) {
              Icon(Icons.AutoMirrored.Sharp.ArrowBack, contentDescription = null)
            }
          }
        )
      },
      contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
      Column(
        modifier = Modifier
          .padding(innerPadding)
          .verticalScroll(rememberScrollState())
          .fillMaxWidth()
          .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        OutlinedTextField(
          modifier = Modifier.fillMaxWidth(),
          label = { Text(stringResource(R.string.name)) },
          value = name,
          onValueChange = {
            name = it
            id = it.lowercase().replace(" ", "")
            packageName = "${packageName.substringBeforeLast(".")}.${
              it.lowercase().replace(" ", "_").replace("-", "_").replace(".", "")
            }"
            mainClass = it.replace(" ", "")
          }
        )

        OutlinedTextField(
          modifier = Modifier.fillMaxWidth(),
          label = { Text(stringResource(R.string.package_name)) },
          value = packageName,
          placeholder = { Text(defaultPackageName) },
          onValueChange = { packageName = it.replace(" ", "") },
          isError = !isValidPackageName,
          supportingText = if (!isValidPackageName) {
            { Text(stringResource(R.string.package_name_already_exists)) }
          } else {
            null
          }
        )

        OutlinedTextField(
          modifier = Modifier.fillMaxWidth(),
          label = { Text("Directory") },
          value = directory,
          onValueChange = { directory = it },
          trailingIcon = {
            IconButton(onClick = {
              openFolder.launch(null)
            }) {
              Icon(Icons.Rounded.Folder, contentDescription = null)
            }
          }
        )

        OutlinedTextField(
          modifier = Modifier.fillMaxWidth(),
          label = { Text("Main Class") },
          value = mainClass,
          onValueChange = { mainClass = it.replace(" ", "") }
        )

        OutlinedTextField(
          modifier = Modifier.fillMaxWidth(),
          label = { Text(stringResource(R.string.author)) },
          value = author,
          placeholder = { Text(defaultAuthorName) },
          onValueChange = { author = it }
        )

        OutlinedTextField(
          modifier = Modifier.fillMaxWidth(),
          label = { Text(stringResource(R.string.description)) },
          value = description,
          onValueChange = { description = it },
          singleLine = false,
          maxLines = 3
        )

        OutlinedTextField(
          modifier = Modifier.fillMaxWidth(),
          label = { Text("Version") },
          value = version,
          onValueChange = { version = it }
        )

        OutlinedTextField(
          modifier = Modifier.fillMaxWidth(),
          label = { Text("ID") },
          value = id,
          onValueChange = { id = it.lowercase().replace(" ", "") },
          isError = id.isBlank()
        )

        OutlinedTextField(
          modifier = Modifier.fillMaxWidth(),
          label = { Text("Website") },
          value = website,
          onValueChange = { website = it }
        )

        OutlinedTextField(
          modifier = Modifier.fillMaxWidth(),
          label = { Text("License") },
          value = license,
          onValueChange = { license = it }
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          OutlinedButton(onClick = onDismiss) {
            Text(stringResource(strings.cancel))
          }

          Spacer(modifier = Modifier.width(8.dp))

          Button(
            onClick = {
              if (isValid) {
                val pluginDir = directory.toFile().resolve(name)
                FileUtils.createOrExistsDir(pluginDir)
                val properties = pluginDir.resolve("plugin.properties")
                FileUtils.createOrExistsFile(properties)

                val pluginInfo = PluginInfo(properties).apply {
                  this.name = name
                  this.pluginFileName = "${name.lowercase().replace(" ", "-")}-all.jar"
                  this.packageName = packageName
                  this.author = author.ifBlank { null }
                  this.description = description.ifBlank { null }
                  this.mainClass = "$packageName.$mainClass"
                  this.version = version.ifBlank { defaultVersion }
                  this.id = id
                  this.website = website.ifBlank { null }
                  this.license = license.ifBlank { null }
                }
                onDismiss()
                onCreate(pluginInfo, pluginDir.absolutePath)
              }
            },
            enabled = isValid
          ) {
            Text(stringResource(R.string.create))
          }
        }
      }
    }
  }
}
