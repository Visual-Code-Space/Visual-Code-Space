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

package com.teixeira.vcspace.screens.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teixeira.vcspace.core.components.editor.FileTabLayout
import com.teixeira.vcspace.editor.CodeEditorView
import com.teixeira.vcspace.viewmodel.editor.EditorViewModel

@Composable
fun EditorScreen(
  viewModel: EditorViewModel = viewModel(),
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  val files = uiState.openedFiles
  val selectedFileIndex = uiState.selectedFileIndex

  val editorMap = remember { mutableStateMapOf<String, CodeEditorView>() }

  Column(modifier = modifier) {
    FileTabLayout(editorViewModel = viewModel)

    val selectedFile = files.getOrNull(selectedFileIndex)

    selectedFile?.let { file ->
      val filePath = file.path
      val context = LocalContext.current

      key(file) {
        val editor = remember(filePath) {
          editorMap.getOrPut(filePath) {
            CodeEditorView(context, file)
          }
        }

        AndroidView(
          factory = { editor },
          modifier = Modifier.fillMaxSize(),
          update = { editorView ->
            editorView.updateFile(file, updateContent = true)
          },
          onRelease = { it.release() }
        )
      }
    } ?: run {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        Text("No opened files")
      }
    }
  }
}
