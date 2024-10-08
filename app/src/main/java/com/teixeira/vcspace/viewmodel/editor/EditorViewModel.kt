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

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.FileUtils
import com.google.gson.Gson
import com.teixeira.vcspace.activities.editor.EditorActivity.Companion.LAST_OPENED_FILES_JSON_PATH
import com.teixeira.vcspace.editor.CodeEditorView
import com.teixeira.vcspace.extensions.toFile
import com.teixeira.vcspace.models.FileHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class EditorViewModel : ViewModel() {
  data class OpenedFile(
    val file: File,
    val isModified: Boolean = false
  )

  data class UiState(
    val openedFiles: List<OpenedFile> = emptyList(),
    val selectedFileIndex: Int = 0
  )

  private val _uiState = MutableStateFlow(UiState())
  val uiState get() = _uiState.asStateFlow()

  private val _editors = mutableStateMapOf<String, CodeEditorView>()
  val editors get() = _editors

  private val _editorConfigMap = mutableStateMapOf<String, Boolean>()
  val editorConfigMap get() = _editorConfigMap

  fun setEditorConfiguredForFile(file: File) {
    _editorConfigMap[file.path] = true
  }

  fun getEditorForFile(
    context: Context,
    file: File
  ): CodeEditorView {
    return _editors.getOrPut(file.path) {
      CodeEditorView(context, file)
    }
  }

  fun getEditorForFile(file: File): CodeEditorView? {
    return _editors[file.path]
  }

  fun getSelectedEditor(): CodeEditorView? {
    return _editors[uiState.value.openedFiles[uiState.value.selectedFileIndex].file.path]
  }

  fun rememberLastFiles() {
    val lastOpenedFiles = Gson().toJson(FileHistory(uiState.value.openedFiles.map { it.file.path }))

    viewModelScope.launch(Dispatchers.IO) {
      File(LAST_OPENED_FILES_JSON_PATH).apply {
        FileUtils.createOrExistsFile(this)
        writeText(lastOpenedFiles)
      }
    }
  }

  fun lastOpenedFiles(): List<File> {
    val file = File(LAST_OPENED_FILES_JSON_PATH)
    if (!file.exists()) return emptyList()

    val fileHistory =
      Gson().fromJson(file.readText(), FileHistory::class.java) ?: return emptyList()
    return fileHistory.lastOpenedFilesPath.map { it.toFile() }
  }

  fun setModified(file: File, modified: Boolean) {
    _uiState.update { currentState ->
      val updatedFiles = currentState.openedFiles.map { openedFile ->
        if (openedFile.file == file) {
          openedFile.copy(isModified = modified)
        } else {
          openedFile
        }
      }
      currentState.copy(openedFiles = updatedFiles)
    }
  }

  suspend fun saveFile(editorView: CodeEditorView? = null) {
    editorView?.saveFile() ?: getSelectedEditor()?.saveFile()
    (editorView?.file ?: getSelectedEditor()?.file)?.let { setModified(it, false) }
  }

  suspend fun saveAll() {
    editors.values.forEach {
      it.saveFile()
      it.file?.let { file -> setModified(file, false) }
    }
  }

  fun addFile(file: File) {
    val openedFile = OpenedFile(file)

    val newOpenedFiles = uiState.value.openedFiles.toMutableList()
    if (!newOpenedFiles.contains(openedFile)) newOpenedFiles.add(openedFile)

    val newSelectedFileIndex = newOpenedFiles.indexOf(openedFile)

    _uiState.value = uiState.value.copy(
      openedFiles = newOpenedFiles,
      selectedFileIndex = newSelectedFileIndex
    )
  }

  fun addFiles(vararg files: File) {
    viewModelScope.launch {
      files.forEach { addFile(it) }
    }
  }

  fun selectFile(index: Int) {
    _uiState.value = uiState.value.copy(selectedFileIndex = index)
  }

  fun closeFile(index: Int) {
    val newOpenedFiles = uiState.value.openedFiles.toMutableList()
    val closingFilePath = newOpenedFiles[index].file.path
    newOpenedFiles.removeAt(index)

    val newSelectedFileIndex = if (newOpenedFiles.isEmpty()) {
      0
    } else {
      index.coerceAtMost(newOpenedFiles.size - 1)
    }

    _editorConfigMap[closingFilePath] = false

    _uiState.value = uiState.value.copy(
      openedFiles = newOpenedFiles,
      selectedFileIndex = newSelectedFileIndex
    )

    _editors.remove(closingFilePath)?.release()
  }

  fun closeOthers(index: Int) {
    val newOpenedFiles = uiState.value.openedFiles.toMutableList()
    val selectedFile = newOpenedFiles[index]
    newOpenedFiles.removeAll { it != selectedFile }
    _uiState.value = uiState.value.copy(
      openedFiles = newOpenedFiles,
      selectedFileIndex = 0
    )
  }

  fun closeAll() {
    _editorConfigMap.clear()

    _uiState.value = uiState.value.copy(openedFiles = emptyList())

    _editors.values.forEach { it.release() }
    _editors.clear()
  }
}
