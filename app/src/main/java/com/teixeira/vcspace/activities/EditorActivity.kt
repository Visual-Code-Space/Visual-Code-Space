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

package com.teixeira.vcspace.activities

import android.os.Build
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.UriUtils
import com.teixeira.vcspace.activities.base.BaseComposeActivity
import com.teixeira.vcspace.app.noLocalProvidedFor
import com.teixeira.vcspace.commandpalette.CommandPaletteManager
import com.teixeira.vcspace.commandpalette.newCommand
import com.teixeira.vcspace.core.settings.Settings.File.rememberShowHiddenFiles
import com.teixeira.vcspace.editor.addBlockComment
import com.teixeira.vcspace.editor.addSingleComment
import com.teixeira.vcspace.editor.events.OnContentChangeEvent
import com.teixeira.vcspace.editor.events.OnKeyBindingEvent
import com.teixeira.vcspace.extensions.open
import com.teixeira.vcspace.extensions.toFile
import com.teixeira.vcspace.plugins.Manifest
import com.teixeira.vcspace.preferences.pluginsPath
import com.teixeira.vcspace.ui.screens.editor.EditorScreen
import com.teixeira.vcspace.ui.screens.editor.EditorViewModel
import com.teixeira.vcspace.ui.screens.editor.components.EditorDrawerSheet
import com.teixeira.vcspace.ui.screens.editor.components.EditorTopBar
import com.teixeira.vcspace.ui.screens.file.FileExplorerViewModel
import io.github.rosemoe.sora.event.ContentChangeEvent
import kotlinx.coroutines.DelicateCoroutinesApi
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

val LocalEditorDrawerState = compositionLocalOf<DrawerState> {
  noLocalProvidedFor("LocalEditorDrawerState")
}

class EditorActivity : BaseComposeActivity() {
  companion object {
    private const val TAG = "EditorActivity"

    const val EXTRA_KEY_PLUGIN_MANIFEST = "plugin_manifest"

    val LAST_OPENED_FILES_JSON_PATH =
      "${PathUtils.getExternalAppFilesPath()}/settings/lastOpenedFile.json"
  }

  private val editorViewModel: EditorViewModel by viewModels()

  @Subscribe(threadMode = ThreadMode.MAIN)
  fun onContentChangeEvent(e: OnContentChangeEvent) {
    Log.d("EditorActivity", "Content change event received: ${e.file?.name}")

    if (e.file != null) {
      editorViewModel.setModified(
        e.file!!,
        e.event.action != ContentChangeEvent.ACTION_SET_NEW_TEXT
      )
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  fun onKeyBindingEvent(event: OnKeyBindingEvent) {
    editorViewModel.setCanEditorHandleCurrentKeyBinding(event.canEditorHandle)
  }

  @OptIn(DelicateCoroutinesApi::class)
  private fun onCreate() {
    CommandPaletteManager.instance.addCommand(
      newCommand("Paste", "Ctrl+V") {
        val editor = editorViewModel.getSelectedEditor()?.editor
        val canEditorHandle = editorViewModel.canEditorHandleCurrentKeyBinding.value

        if (!canEditorHandle) {
          editor?.pasteText()
        }
      },
      newCommand("Copy", "Ctrl+C") {
        val editor = editorViewModel.getSelectedEditor()?.editor
        val canEditorHandle = editorViewModel.canEditorHandleCurrentKeyBinding.value

        if (!canEditorHandle && editor?.cursor?.isSelected == true) {
          editor.copyText()
        }
      },
      newCommand("Cut", "Ctrl+X") {
        val editor = editorViewModel.getSelectedEditor()?.editor
        val canEditorHandle = editorViewModel.canEditorHandleCurrentKeyBinding.value

        if (!canEditorHandle && editor?.cursor?.isSelected == true) {
          editor.cutText()
        }
      },
      newCommand("Copy Path of Active File", "Shift+Alt+C") {
        val file = editorViewModel.getSelectedEditor()?.file
        ClipboardUtils.copyText(file?.absolutePath)
        ToastUtils.showShort("Copied path of active file: ${file?.name}")
      },
      newCommand("Copy File Name", "Shift+Alt+F") {
        val file = editorViewModel.getSelectedEditor()?.file
        ClipboardUtils.copyText(file?.name)
        ToastUtils.showShort("Copied file name: ${file?.name}")
      },
      newCommand("Undo", "Ctrl+Z") {
        val editor = editorViewModel.getSelectedEditor()?.editor
        val canEditorHandle = editorViewModel.canEditorHandleCurrentKeyBinding.value

        if (!canEditorHandle) {
          editor?.undo()
        }
      },
      newCommand("Redo", "Ctrl+Y") {
        val editor = editorViewModel.getSelectedEditor()?.editor
        val canEditorHandle = editorViewModel.canEditorHandleCurrentKeyBinding.value

        if (!canEditorHandle) {
          editor?.redo()
        }
      },
      newCommand("Toggle Line Comment", "Ctrl+/") {
        val editor = editorViewModel.getSelectedEditor()?.editor
        val canEditorHandle = editorViewModel.canEditorHandleCurrentKeyBinding.value

        val commentRule = editor?.commentRule
        if (editor != null && !canEditorHandle) {
          if (!editor.cursor.isSelected) {
            addSingleComment(commentRule, editor.text)
          } else {
            addBlockComment(commentRule, editor.text)
          }
        }
      },
      newCommand("Settings", "Ctrl+,") {
        open(SettingsActivity::class.java)
      },
      newCommand("Manage Plugins", "Ctrl+P") {
        open(PluginsActivity::class.java)
      }
    )
  }

  @Composable
  override fun MainScreen() {
    val fileExplorerViewModel: FileExplorerViewModel = viewModel()
    val editorViewModel: EditorViewModel = viewModel()

    val editorUiState by editorViewModel.uiState.collectAsStateWithLifecycle()
    val openedFiles = editorUiState.openedFiles

    val showHiddenFiles by rememberShowHiddenFiles()

    observeLifecycleEvents { event ->
      when (event) {
        Lifecycle.Event.ON_CREATE -> {
          EventBus.getDefault().register(this@EditorActivity)

          // Open plugin files if opened from PluginsActivity
          run {
            @Suppress("DEPRECATION")
            val manifest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
              intent.getSerializableExtra(EXTRA_KEY_PLUGIN_MANIFEST, Manifest::class.java)
            } else intent.getSerializableExtra(EXTRA_KEY_PLUGIN_MANIFEST) as? Manifest

            if (manifest != null) {
              val pluginPath = "$pluginsPath/${manifest.packageName}"
              val filesToOpen = arrayOf(
                "$pluginPath/manifest.json".toFile(),
                "$pluginPath/${manifest.scripts.first().name}".toFile()
              )
              editorViewModel.addFiles(*filesToOpen)
              fileExplorerViewModel.setCurrentPath(
                filesToOpen.last().absolutePath,
                showHiddenFiles
              )
            }
          }

          val externalFileUri = intent.data
          if (externalFileUri != null) {
            editorViewModel.addFile(UriUtils.uri2File(externalFileUri))
            externalFileUri.path?.let {
              fileExplorerViewModel.setCurrentPath(
                path = it,
                showHiddenFiles = showHiddenFiles
              )
            }
          }

          onCreate()
        }

        Lifecycle.Event.ON_PAUSE -> {
          editorViewModel.rememberLastFiles()
        }

        Lifecycle.Event.ON_DESTROY -> {
          editorViewModel.rememberLastFiles()
          EventBus.getDefault().unregister(this@EditorActivity)
          clearCache()
        }

        Lifecycle.Event.ON_START -> {}
        Lifecycle.Event.ON_RESUME -> {}
        Lifecycle.Event.ON_STOP -> clearCache()
        Lifecycle.Event.ON_ANY -> {}
      }
    }

    ProvideEditorCompositionLocals {
      ModalNavigationDrawer(
        modifier = Modifier
          .fillMaxSize()
          .imePadding(),
        drawerState = LocalEditorDrawerState.current,
        gesturesEnabled = openedFiles.isEmpty(),
        drawerContent = {
          ModalDrawerSheet(
            drawerState = LocalEditorDrawerState.current,
            modifier = Modifier
              .fillMaxWidth(fraction = 0.8f)
          ) {
            EditorDrawerSheet(
              fileExplorerViewModel = fileExplorerViewModel,
              editorViewModel = editorViewModel
            )
          }
        }
      ) {
        Scaffold(
          modifier = Modifier.fillMaxSize(),
          topBar = {
            EditorTopBar(
              editorViewModel = editorViewModel
            )
          }
        ) { innerPadding ->
          EditorScreen(
            viewModel = editorViewModel,
            fileExplorerViewModel = fileExplorerViewModel,
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          )
        }
      }
    }
  }

  @Composable
  private fun ProvideEditorCompositionLocals(content: @Composable () -> Unit) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    CompositionLocalProvider(
      LocalEditorDrawerState provides drawerState,
      content = content
    )
  }

  @Composable
  private fun observeLifecycleEvents(onStateChanged: (Lifecycle.Event) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
      val observer = LifecycleEventObserver { _, event ->
        onStateChanged(event)
      }

      lifecycleOwner.lifecycle.addObserver(observer)

      onDispose {
        lifecycleOwner.lifecycle.removeObserver(observer)
      }
    }
  }

  private fun clearCache(): Boolean {
    return FileUtils.deleteAllInDir(cacheDir).also {
      Log.i(TAG, "Cache cleared 😊")
    }
  }
}
