package com.raredev.vcspace.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.Observer
import java.io.File
import java.util.ArrayList
import java.util.List

class EditorViewModel : ViewModel() {
  internal val _displayedFile = MutableLiveData(-1)

  private val editorFiles = MutableLiveData<MutableList<File>>(ArrayList())

  private val mCurrentFile = MutableLiveData<Pair<Int, File?>?>(null)

  var displayedFileIndex: Int
    get() = _displayedFile.value!!
    set(value) {
      _displayedFile.value = value
    }

  fun getDisplayedFile(): MutableLiveData<Int> {
    return _displayedFile
  }

  fun setCurrentFile(index: Int, file: File?) {
    mCurrentFile.value = index to file
    displayedFileIndex = index
  }

  fun addFile(file: File) {
    val files = editorFiles.value ?: mutableListOf()
    files.add(file)
    editorFiles.value = files
  }

  fun removeFile(index: Int) {
    val files = editorFiles.value ?: mutableListOf()
    files.removeAt(index)
    editorFiles.value = files
  }

  fun removeAllFiles() {
    editorFiles.value = ArrayList()
  }

  fun getOpenedFileCount(): Int {
    return editorFiles.value!!.size
  }

  fun getOpenedFiles(): MutableList<File> {
    return editorFiles.value!!
  }

  fun observeFiles(lifecycleOwner: LifecycleOwner?, observer: Observer<MutableList<File>?>?) {
    editorFiles.observe(lifecycleOwner!!, observer!!)
  }

  fun getCurrentFileIndex(): Int {
    return mCurrentFile.value?.first ?: -1
  }

  fun getCurrentFile(): File? {
    return mCurrentFile.value?.second
  }
}