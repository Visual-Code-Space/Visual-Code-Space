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

package com.teixeira.vcspace.ui.filetree

import android.os.Parcelable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
class FileListLoader(
  private val cacheFiles: MutableMap<String, MutableList<File>> = mutableMapOf()
) : Parcelable {

  private fun getFileList(file: File): List<File> {
    return (file.listFiles() ?: emptyArray()).run {
      sortedWith(compareBy<File> { if (it.isFile) 1 else 0 }.thenBy { it.name.lowercase() })
    }
  }

  suspend fun loadFileList(path: String) = withContext(Dispatchers.IO) {
    val result = cacheFiles[path] ?: run {
      val files = getFileList(File(path))
      cacheFiles[path] = files.toMutableList()

      // load sub directory, but only load one level
      files.forEach {
        if (it.isDirectory) {
          cacheFiles[it.absolutePath] = getFileList(it).toMutableList()
        }
      }

      files.toMutableList()
    }

    result
  }

  fun getCacheFileList(path: String) = cacheFiles[path] ?: emptyList()

  fun removeFileInCache(currentFile: File): Boolean {
    if (currentFile.isDirectory) {
      cacheFiles.remove(currentFile.absolutePath)
    }

    val parent = currentFile.parentFile
    val parentPath = parent?.absolutePath
    val parentFiles = cacheFiles[parentPath]
    return parentFiles?.remove(currentFile) ?: false
  }


  override fun toString(): String {
    return "FileListLoader(cacheFiles=$cacheFiles)"
  }
}