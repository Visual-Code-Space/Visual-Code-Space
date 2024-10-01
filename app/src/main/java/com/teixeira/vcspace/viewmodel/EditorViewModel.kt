package com.teixeira.vcspace.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import java.io.File

class EditorViewModel : ViewModel() {

  private val _action = MutableLiveData<EditorAction>()
  private val _files = MutableLiveData<MutableList<File>>(mutableListOf())
  private val _selectedFile = MutableLiveData<Pair<Int, File?>>(-1 to null)

  val openedFiles: List<File>
    get() = _files.value!!

  val fileCount: Int
    get() = _files.value!!.size

  val selectedFile: File?
    get() = _selectedFile.value!!.second

  val selectedFileIndex: Int
    get() = _selectedFile.value!!.first

  fun openFile(file: File) {
    executeAction(EditorAction.OpenFileAction(file))
  }

  fun closeFile(fileIndex: Int) {
    executeAction(EditorAction.CloseFileAction(fileIndex))
  }

  fun closeOthers() {
    executeAction(EditorAction.CloseOthersAction)
  }

  fun closeAllFiles() {
    executeAction(EditorAction.CloseAllAction)
  }

  fun executeAction(action: EditorAction) {
    this._action.value = action
  }

  fun addFile(file: File) {
    val files = this._files.value!!
    files.add(file)
    this._files.value = files
  }

  fun updateFile(index: Int, file: File) {
    val files = this._files.value!!
    files[index] = file
    this._files.value = files
  }

  fun removeFile(index: Int) {
    val files = this._files.value!!
    files.removeAt(index)
    this._files.value = files

    if (files.isEmpty()) setSelectedFile(-1, null)
  }

  fun removeAllFiles() {
    val files = this._files.value!!
    files.clear()
    this._files.value = files
    setSelectedFile(-1, null)
  }

  fun setSelectedFile(index: Int, file: File? = openedFiles[index]) {
    this._selectedFile.value = index to file
  }

  fun observeAction(lifecycleOwner: LifecycleOwner, observer: Observer<EditorAction>) {
    this._action.observe(lifecycleOwner, observer)
  }

  fun observeFiles(lifecycleOwner: LifecycleOwner, observer: Observer<MutableList<File>>) {
    this._files.observe(lifecycleOwner, observer)
  }

  fun observeSelectedFile(lifecycleOwner: LifecycleOwner, observer: Observer<Pair<Int, File?>>) {
    this._selectedFile.observe(lifecycleOwner, observer)
  }

  sealed interface EditorAction {
    data class OpenFileAction(val file: File) : EditorAction

    data class CloseFileAction(val fileIndex: Int) : EditorAction

    data object CloseOthersAction : EditorAction

    data object CloseAllAction : EditorAction
  }
}
