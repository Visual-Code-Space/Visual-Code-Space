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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teixeira.vcspace.activities.BaseComposeActivity
import com.teixeira.vcspace.screens.editor.EditorScreen
import com.teixeira.vcspace.screens.editor.components.EditorDrawerSheet
import com.teixeira.vcspace.screens.editor.components.EditorTopBar
import com.teixeira.vcspace.viewmodel.editor.EditorViewModel
import com.teixeira.vcspace.viewmodel.file.FileExplorerViewModel

class EditorActivity : BaseComposeActivity() {
  @Composable
  override fun MainScreen() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val fileExplorerViewModel: FileExplorerViewModel = viewModel()
    val editorViewModel: EditorViewModel = viewModel()

    ModalNavigationDrawer(
      modifier = Modifier.fillMaxSize(),
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
