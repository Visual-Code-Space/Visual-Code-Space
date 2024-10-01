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

package com.teixeira.vcspace.viewmodel.editor

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class EditorViewModel : ViewModel() {
  data class UiState(
    val openedFiles: List<File> = emptyList(),
    val selectedFileIndex: Int = 0
  )

  private val _uiState = MutableStateFlow(UiState())
  val uiState get() = _uiState.asStateFlow()

  fun addFile(file: File) {
    val newOpenedFiles = uiState.value.openedFiles.toMutableList()
    if (!newOpenedFiles.contains(file)) newOpenedFiles.add(file)

    val newSelectedFileIndex = newOpenedFiles.indexOf(file)

    _uiState.value = uiState.value.copy(
      openedFiles = newOpenedFiles,
      selectedFileIndex = newSelectedFileIndex
    )
  }

  fun selectFile(index: Int) {
    _uiState.value = uiState.value.copy(selectedFileIndex = index)
  }

  fun closeFile(index: Int) {
    val newOpenedFiles = uiState.value.openedFiles.toMutableList()
    newOpenedFiles.removeAt(index)

    val newSelectedFileIndex = if (newOpenedFiles.isEmpty()) {
      0
    } else {
      index.coerceAtMost(newOpenedFiles.size - 1)
    }

    _uiState.value = uiState.value.copy(
      openedFiles = newOpenedFiles,
      selectedFileIndex = newSelectedFileIndex
    )
  }
}
