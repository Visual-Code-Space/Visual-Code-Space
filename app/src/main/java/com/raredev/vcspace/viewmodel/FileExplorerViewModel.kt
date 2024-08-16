package com.raredev.vcspace.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.PathUtils
import com.raredev.vcspace.utils.getParentDirPath
import com.raredev.vcspace.preferences.fileShowhiddenfiles
import java.io.File
import java.util.Arrays
import java.util.Comparator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FileExplorerViewModel : ViewModel() {

  private val _files = MutableLiveData<List<File>>(emptyList())
  private val _currentPath = MutableLiveData(PathUtils.getRootPathExternalFirst())

  val files: LiveData<List<File>> = _files
  val currentPath: LiveData<String> = _currentPath

  fun backPath() {
    if (_currentPath.value.equals(PathUtils.getRootPathExternalFirst())) {
      return
    }
    setCurrentPath(getParentDirPath(_currentPath.value!!))
  }

  fun setCurrentPath(path: String) {
    _currentPath.value = path
    refreshFiles()
  }

  fun refreshFiles() {
    viewModelScope.launch(Dispatchers.IO) {
      val listFiles = _currentPath.value?.let { File(it).listFiles() }

      val files = mutableListOf<File>()

      if (listFiles != null) {
        Arrays.sort(listFiles, FOLDER_FIRST_ORDER)
        for (file in listFiles) {
          if (file.isHidden && !fileShowhiddenfiles) {
            continue
          }
          files.add(file)
        }
      }
      withContext(Dispatchers.Main) { _files.value = files }
    }
  }

  companion object {
    val FOLDER_FIRST_ORDER: Comparator<File> =
      compareBy<File> { file -> if (file.isFile) 1 else 0 }.thenBy { it.name.lowercase() }
  }
}
