package com.raredev.vcspace.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import java.io.File

class EditorViewModel : ViewModel() {

  private val files = MutableLiveData<MutableList<File>>(ArrayList())
  private val _selectedFilePosition = MutableLiveData<Int>(-1)

  val selectedFilePosition: LiveData<Int> = _selectedFilePosition

  fun addFile(file: File) {
    val files = this.files.value ?: mutableListOf()
    files.add(file)
    this.files.value = files
  }

  fun updateFile(position: Int, file: File) {
    val files = this.files.value ?: mutableListOf()
    files[position] = file
    this.files.value = files
  }

  fun removeFile(position: Int) {
    val files = this.files.value ?: mutableListOf()
    files.removeAt(position)
    this.files.value = files
  }

  fun removeAllFiles() {
    val files = this.files.value ?: mutableListOf()
    files.clear()
    this.files.value = files

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

  fun getSelectedFile(): File {
    return getOpenedFiles()[getSelectedFilePos()]
  }

  fun observeFiles(lifecycleOwner: LifecycleOwner, observer: Observer<MutableList<File>>) {
    files.observe(lifecycleOwner, observer)
  }
}
