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

package com.teixeira.vcspace.activities.editor

import android.util.Log
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blankj.utilcode.util.PathUtils
import com.teixeira.vcspace.activities.BaseComposeActivity
import com.teixeira.vcspace.editor.events.OnContentChangeEvent
import com.teixeira.vcspace.screens.editor.EditorScreen
import com.teixeira.vcspace.screens.editor.components.EditorDrawerSheet
import com.teixeira.vcspace.screens.editor.components.EditorTopBar
import com.teixeira.vcspace.viewmodel.editor.EditorViewModel
import com.teixeira.vcspace.viewmodel.file.FileExplorerViewModel
import io.github.rosemoe.sora.event.ContentChangeEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class EditorActivity : BaseComposeActivity() {
  companion object {
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

  @Composable
  override fun MainScreen() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val fileExplorerViewModel: FileExplorerViewModel = viewModel()
    val editorViewModel: EditorViewModel = viewModel()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
      val observer = LifecycleEventObserver { _, event ->
        when (event) {
          Lifecycle.Event.ON_CREATE -> {
            EventBus.getDefault().register(this@EditorActivity)
          }

          Lifecycle.Event.ON_PAUSE -> {
            editorViewModel.rememberLastFiles()
          }

          Lifecycle.Event.ON_DESTROY -> {
            editorViewModel.rememberLastFiles()
            EventBus.getDefault().unregister(this@EditorActivity)
          }

          Lifecycle.Event.ON_START -> {}
          Lifecycle.Event.ON_RESUME -> {}
          Lifecycle.Event.ON_STOP -> {}
          Lifecycle.Event.ON_ANY -> {}
        }
      }

      lifecycleOwner.lifecycle.addObserver(observer)

      onDispose {
        lifecycleOwner.lifecycle.removeObserver(observer)
      }
    }

    ModalNavigationDrawer(
      modifier = Modifier
        .fillMaxSize()
        .imePadding(),
      drawerState = drawerState,
      gesturesEnabled = false,
      drawerContent = {
        ModalDrawerSheet(
          drawerState = drawerState,
          modifier = Modifier
            .fillMaxWidth(fraction = 0.8f)
            .systemBarsPadding()
        ) {

          EditorDrawerSheet(
            fileExplorerViewModel = fileExplorerViewModel,
            editorViewModel = editorViewModel,
            drawerState = drawerState
          )
        }
      }
    ) {
      Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
          EditorTopBar(
            editorViewModel = editorViewModel,
            drawerState = drawerState
          )
        }
      ) {
        EditorScreen(
          viewModel = editorViewModel,
          modifier = Modifier
            .fillMaxSize()
            .padding(it)
        )
      }
    }
  }
}
