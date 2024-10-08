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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Process
import android.util.Log
import android.view.View
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Redo
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.SaveAs
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.UriUtils
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.google.android.material.snackbar.Snackbar
import com.hzy.libp7zip.P7ZipApi
import com.teixeira.vcspace.PYTHON_PACKAGE_URL_32_BIT
import com.teixeira.vcspace.PYTHON_PACKAGE_URL_64_BIT
import com.teixeira.vcspace.activities.TerminalActivity
import com.teixeira.vcspace.activities.editor.LocalDrawerState
import com.teixeira.vcspace.core.components.Tooltip
import com.teixeira.vcspace.core.settings.Settings.EditorTabs.rememberAutoSave
import com.teixeira.vcspace.editor.CodeEditorView
import com.teixeira.vcspace.editor.events.OnContentChangeEvent
import com.teixeira.vcspace.preferences.pythonDownloaded
import com.teixeira.vcspace.preferences.pythonExtracted
import com.teixeira.vcspace.resources.R.string
import com.teixeira.vcspace.utils.launchWithProgressDialog
import com.teixeira.vcspace.viewmodel.editor.EditorViewModel
import io.github.rosemoe.sora.event.ContentChangeEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorTopBar(
  modifier: Modifier = Modifier,
  editorViewModel: EditorViewModel
) {
  val scope = rememberCoroutineScope()
  val drawerState = LocalDrawerState.current

  var showMenu by remember { mutableStateOf(false) }
  val showFileMenu = remember { mutableStateOf(false) }

  val editors = editorViewModel.editors
  val uiState by editorViewModel.uiState.collectAsStateWithLifecycle()

  val selectedFileIndex = uiState.selectedFileIndex
  val selectedFile = uiState.openedFiles.getOrNull(selectedFileIndex)

  val selectedEditor = selectedFile?.let { editors[it.file.path] }

  var canUndo by remember { mutableStateOf(false) }
  var canRedo by remember { mutableStateOf(false) }

  val areModifiedFiles by remember(selectedFileIndex) {
    derivedStateOf {
      editors.any { it.value.modified }
    }
  }

  val autoSave by rememberAutoSave()

  LaunchedEffect(selectedEditor, autoSave) {
    selectedEditor?.let { editorView ->
      canUndo = editorView.canUndo()
      canRedo = editorView.canRedo()

      editorView.editor.subscribeEvent(ContentChangeEvent::class.java) { event, _ ->
        EventBus.getDefault().post(OnContentChangeEvent(selectedFile.file, event))
        editorView.setModified(event.action != ContentChangeEvent.ACTION_SET_NEW_TEXT)
        canUndo = editorView.canUndo()
        canRedo = editorView.canRedo()

        if (autoSave) {
          scope.launch {
            delay(100)
            editorViewModel.saveFile()
          }
        }
      }
    }
  }

  val context = LocalContext.current
  val view = LocalView.current

  var isKeyboardOpen by remember { mutableStateOf(KeyboardUtils.isSoftInputVisible(context as Activity)) }
  KeyboardUtils.registerSoftInputChangedListener(context as Activity) {
    isKeyboardOpen = KeyboardUtils.isSoftInputVisible(context)
  }

  TopAppBar(
    modifier = modifier,
    title = {
      Text(
        text = stringResource(id = string.app_name),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
      )
    },
    navigationIcon = {
      Tooltip(stringResource(id = string.open_drawer)) {
        IconButton(onClick = {
          scope.launch {
            drawerState.apply {
              if (isOpen) close() else open()
            }
          }
        }) {
          Icon(
            imageVector = Icons.Rounded.Menu,
            contentDescription = stringResource(id = string.open_drawer)
          )
        }
      }
    },
    actions = {
      AnimatedVisibility(
        visible = selectedEditor?.file?.extension == "py"
      ) {
        Tooltip(stringResource(id = string.execute)) {
          IconButton(
            onClick = {
              downloadPythonPackage(
                scope = scope,
                context = context,
                view = view
              ) {
                context.startActivity(
                  Intent(context, TerminalActivity::class.java).apply {
                    putExtra(
                      TerminalActivity.KEY_PYTHON_FILE_PATH,
                      selectedEditor?.file?.absolutePath
                    )
                  }
                )
              }
            }
          ) {
            Icon(
              imageVector = Icons.Rounded.PlayArrow,
              contentDescription = null
            )
          }
        }
      }

      AnimatedVisibility(
        visible = isKeyboardOpen
      ) {
        Row {
          Tooltip(stringResource(id = string.editor_undo)) {
            IconButton(
              onClick = { selectedEditor?.undo() },
              enabled = canUndo
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Rounded.Undo,
                contentDescription = null
              )
            }
          }

          Tooltip(stringResource(id = string.editor_redo)) {
            IconButton(
              onClick = { selectedEditor?.redo() },
              enabled = canRedo
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Rounded.Redo,
                contentDescription = null
              )
            }
          }
        }
      }

      Box {
        Tooltip("Menu") {
          IconButton(
            onClick = { showMenu = !showMenu }
          ) {
            Icon(
              imageVector = Icons.Rounded.MoreVert,
              contentDescription = null
            )
          }
        }

        DropdownMenu(
          expanded = showMenu,
          offset = DpOffset((-5).dp, 0.dp),
          shape = MaterialTheme.shapes.medium,
          onDismissRequest = { showMenu = false }
        ) {
          DropdownMenuItem(
            text = { Text(stringResource(id = string.editor_search)) },
            leadingIcon = {
              Icon(
                Icons.Rounded.Search,
                contentDescription = null
              )
            },
            enabled = selectedEditor != null,
            onClick = {
              selectedEditor?.beginSearchMode()
              showMenu = false
            }
          )

          DropdownMenuItem(
            text = { Text(stringResource(id = string.file)) },
            leadingIcon = {
              Icon(
                Icons.Rounded.Folder,
                contentDescription = null
              )
            },
            trailingIcon = {
              Icon(
                Icons.Rounded.ChevronRight,
                contentDescription = null
              )
            },
            onClick = {
              showFileMenu.value = !showFileMenu.value
              showMenu = false
            }
          )
        }

        FileMenu(
          showFileMenu = showFileMenu,
          editorViewModel = editorViewModel,
          editor = selectedEditor,
          scope = scope,
          modified = selectedFile?.isModified == true,
          areModifiedFiles = areModifiedFiles
        )
      }
    }
  )
}

@Composable
fun FileMenu(
  showFileMenu: MutableState<Boolean>,
  editorViewModel: EditorViewModel,
  editor: CodeEditorView?,
  scope: CoroutineScope,
  modified: Boolean,
  areModifiedFiles: Boolean
) {
  val createFile = rememberLauncherForActivityResult(
    ActivityResultContracts.CreateDocument("text/*")
  ) {
    if (it != null) editorViewModel.addFile(UriUtils.uri2File(it))
  }

  val openFile = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
    if (it != null) editorViewModel.addFile(UriUtils.uri2File(it))
  }

  DropdownMenu(
    shape = MaterialTheme.shapes.medium,
    expanded = showFileMenu.value,
    offset = DpOffset((-5).dp, 0.dp),
    onDismissRequest = { showFileMenu.value = false }
  ) {
    DropdownMenuItem(
      text = { Text(stringResource(id = string.file_new)) },
      leadingIcon = {
        Icon(
          Icons.Rounded.Add,
          contentDescription = null
        )
      },
      onClick = {
        createFile.launch("filename.txt")
        showFileMenu.value = false
      }
    )

    DropdownMenuItem(
      text = { Text(stringResource(id = string.file_open)) },
      leadingIcon = {
        Icon(
          Icons.Rounded.FileOpen,
          contentDescription = null
        )
      },
      onClick = {
        openFile.launch(arrayOf("text/*"))
        showFileMenu.value = false
      }
    )

    DropdownMenuItem(
      text = { Text(stringResource(id = string.file_save)) },
      leadingIcon = {
        Icon(
          Icons.Rounded.Save,
          contentDescription = null
        )
      },
      enabled = modified,
      onClick = {
        scope.launch {
          editorViewModel.saveFile(editor)
        }
        showFileMenu.value = false
      }
    )

    DropdownMenuItem(
      text = { Text(stringResource(id = string.file_save_as)) },
      leadingIcon = {
        Icon(
          Icons.Rounded.SaveAs,
          contentDescription = null
        )
      },
      enabled = false,
      onClick = {
        showFileMenu.value = false
        // implement save as
      }
    )

    DropdownMenuItem(
      text = { Text(stringResource(id = string.file_save_all)) },
      leadingIcon = {
        Icon(
          Icons.Rounded.Save,
          contentDescription = null
        )
      },
      enabled = areModifiedFiles,
      onClick = {
        scope.launch {
          editorViewModel.saveAll()
        }
        showFileMenu.value = false
      }
    )

    DropdownMenuItem(
      text = { Text(stringResource(id = string.file_reload)) },
      leadingIcon = {
        Icon(
          Icons.Rounded.Refresh,
          contentDescription = null
        )
      },
      enabled = editor != null,
      onClick = {
        editor?.confirmReload()
      }
    )
  }
}

private fun extractPythonFile(
  scope: CoroutineScope,
  context: Context,
  filePath: String,
  onDone: Runnable
) {
  if (pythonDownloaded) {
    onDone.run()
  } else {
    scope.launchWithProgressDialog(
      uiContext = context,
      context = Dispatchers.IO,
      configureBuilder = {
        it.setMessage(string.python_extracting_python_compiler)
          .setCancelable(false)
      },
      invokeOnCompletion = { throwable ->
        if (throwable == null) {
          pythonDownloaded = true
          onDone.run()
        }
      }
    ) { _, _ ->
      File(filePath).inputStream().use { temp7zStream ->
        val file = File("${context.filesDir.absolutePath}/python.7z").apply { createNewFile() }
        Files.copy(temp7zStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
        val exitCode =
          P7ZipApi.executeCommand("7z x ${file.absolutePath} -o${context.filesDir.absolutePath}")
        Log.d("EditorActivity", "extractFiles: $exitCode")
        file.delete()
      }
    }
  }
}

private fun downloadPythonPackage(
  scope: CoroutineScope,
  context: Context,
  view: View,
  onDownloaded: () -> Unit
) {
  if (pythonDownloaded) {
    onDownloaded()
    return
  }

  if (pythonExtracted) {
    FileUtils.deleteAllInDir(context.filesDir)
  }

  val url = if (Process.is64Bit()) PYTHON_PACKAGE_URL_64_BIT else PYTHON_PACKAGE_URL_32_BIT
  val outputFile = File(context.filesDir, "python.7z")

  scope.launchWithProgressDialog(
    uiContext = context,
    context = Dispatchers.IO,
    configureBuilder = {
      it.setTitle("Downloading Python")
        .setMessage(string.python_downloading_python_compiler)
        .setCancelable(false)
        .setIndeterminate(false)
        .setMax(100)
    }
  ) { builder, dialog ->
    PRDownloader.download(url, outputFile.parent, outputFile.name)
      .build()
      .setOnProgressListener {
        val progress = (it.currentBytes * 100 / it.totalBytes).toInt()
        builder.setProgress(progress).setMessage("Downloading... $progress%")
      }
      .start(object : OnDownloadListener {
        override fun onDownloadComplete() {
          extractPythonFile(
            scope = scope,
            context = context,
            filePath = outputFile.absolutePath
          ) {
            outputFile.delete()
            pythonDownloaded = true
            onDownloaded()
          }
        }

        override fun onError(error: Error) {
          dialog.dismiss()

          Snackbar.make(
            view,
            if (error.isConnectionError) "Connection failed!" else if (error.isServerError) "Server error!" else "Download failed! Something went wrong.",
            Snackbar.LENGTH_SHORT
          ).setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show()
        }
      })
  }
}