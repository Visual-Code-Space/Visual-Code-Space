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

package com.teixeira.vcspace.viewmodel.file

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.PathUtils
import com.teixeira.vcspace.preferences.fileShowhiddenfiles
import com.teixeira.vcspace.utils.getParentDirPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.Arrays

class FileExplorerViewModel : ViewModel() {
  private val _files = MutableStateFlow<List<File>>(emptyList())
  private val _currentPath = MutableStateFlow(PathUtils.getRootPathExternalFirst())

  val files get() = _files.asStateFlow()
  val currentPath get() = _currentPath.asStateFlow()

  fun backPath() {
    if (_currentPath.value.equals(PathUtils.getRootPathExternalFirst())) {
      return
    }
    setCurrentPath(getParentDirPath(_currentPath.value))
  }

  fun setCurrentPath(path: String) {
    _currentPath.value = path
    refreshFiles()
  }

  fun refreshFiles() {
    viewModelScope.launch(Dispatchers.IO) {
      val listFiles = _currentPath.value?.let { File(it).listFiles() }

      _files.update {
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
        files
      }
    }
  }

  companion object {
    val FOLDER_FIRST_ORDER: Comparator<File> =
      compareBy<File> { file -> if (file.isFile) 1 else 0 }.thenBy { it.name.lowercase() }
  }
}