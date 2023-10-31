package com.raredev.vcspace.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.io.File

class EditorViewModel: ViewModel() {

  private val _files = MutableLiveData<MutableList<File>>(ArrayList())
  private val _selectedFilePosition = MutableLiveData<Int>(-1)

  val files: LiveData<MutableList<File>> = _files
  val selectedFilePosition: LiveData<Int> = _selectedFilePosition

  fun addFile(file: File) {
    val files = _files.value
    files?.add(file)
    _files.value = files
  }

  fun removeFile(position: Int) {
    val files = _files.value
    files?.removeAt(position)
    _files.value = files
  }

  fun removeAllFiles() {
    val files = _files.value
    files?.clear()
    _files.value = files

    setSelectedFile(-1)
  }

  fun setSelectedFile(index: Int) {
    _selectedFilePosition.value = index
  }

  fun getOpenedFiles(): List<File> {
    return files.value ?: mutableListOf()
  }

  fun getFileCount(): Int {
    return files.value?.size ?: 0
  }

  fun getSelectedFilePos(): Int {
    return selectedFilePosition.value ?: -1
  }

  fun getSelectedFile(): File? {
    return getOpenedFiles().get(getSelectedFilePos())
  }
}
