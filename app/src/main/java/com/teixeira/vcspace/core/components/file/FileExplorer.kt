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

package com.teixeira.vcspace.core.components.file

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.teixeira.vcspace.core.components.PathListView
import com.teixeira.vcspace.core.settings.Settings.File.rememberShowHiddenFiles
import com.teixeira.vcspace.extensions.openFile
import com.teixeira.vcspace.extensions.toFile
import com.teixeira.vcspace.ui.screens.editor.EditorViewModel
import com.teixeira.vcspace.ui.screens.file.FileExplorerViewModel
import com.teixeira.vcspace.utils.ApkInstaller
import com.teixeira.vcspace.utils.isValidTextFile
import java.io.File

@Composable
fun FileExplorer(
  viewModel: FileExplorerViewModel,
  editorViewModel: EditorViewModel,
  selectedFile: EditorViewModel.OpenedFile? = null,
  modifier: Modifier = Modifier,
  itemModifier: Modifier = Modifier,
  onFileLongClick: ((File) -> Unit)? = null,
  onFileClick: ((File) -> Unit)? = null
) {
  val files by viewModel.files.collectAsStateWithLifecycle()
  val currentPath by viewModel.currentPath.collectAsStateWithLifecycle()

  val context = LocalContext.current

  val showHiddenFiles by rememberShowHiddenFiles()

  LaunchedEffect(showHiddenFiles) { viewModel.refreshFiles(showHiddenFiles) }

  PathListView(
    path = currentPath.toFile(),
    modifier = Modifier.padding(start = 5.dp)
  ) {
    viewModel.setCurrentPath(it.absolutePath, showHiddenFiles)
  }

  FileList(
    files = files,
    modifier = modifier,
    selectedFile = selectedFile,
    itemModifier = itemModifier,
    onFileLongClick = onFileLongClick,
    onFileClick = { file ->
      viewModel.setCurrentPath(file.absolutePath, showHiddenFiles)

      if (!file.isDirectory) {
        if (file.name.endsWith(".apk")) {
          ApkInstaller.installApplication(context, file)
        } else if (isValidTextFile(file)) {
          editorViewModel.addFile(file)
          onFileClick?.invoke(file)
        } else {
          context.openFile(file)
        }
      }
    }
  )
}