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

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import com.teixeira.vcspace.file.File
import com.teixeira.vcspace.file.wrapFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File as JFile

class FileListLoader(
    private val cacheFiles: MutableMap<String, CacheEntry> = mutableMapOf()
) {

    private fun getFileList(file: File): List<File> {
        return (file.listFiles() ?: emptyArray()).run {
            sortedWith(compareBy<File> { if (it.isFile) 1 else 0 }.thenBy { it.name.lowercase() })
        }
    }

    suspend fun loadFileList(
        prefetchScope: CoroutineScope,
        path: File,
        additionalDepth: Int = 1
    ): List<File> = withContext(Dispatchers.Main) {
        when (val value = cacheFiles[path.absolutePath]) {
            null -> {
                val deferred = async(Dispatchers.IO) {
                    getFileList(path).toMutableList()
                }
                cacheFiles[path.absolutePath] = Loading(deferred)
                val res = deferred.await()
                cacheFiles[path.absolutePath] = Loaded(res)
                if (additionalDepth > 0) {
                    res.forEach { child ->
                        prefetchScope.launch {
                            loadFileList(prefetchScope, child, additionalDepth - 1)
                        }
                    }
                }
                res
            }

            is Loading -> {
                value.deferred.await()
            }

            is Loaded -> {
                value.children
            }
        }
    }

    fun getCacheFileList(path: File) = cacheFiles[path.absolutePath]?.getFiles() ?: emptyList()

    fun removeFileInCache(currentFile: File): Boolean {
        if (currentFile.isDirectory) {
            cacheFiles.remove(currentFile.absolutePath)
        }

        val parent = currentFile.parentFile
        val parentPath = parent?.absolutePath
        val parentFiles = (cacheFiles[parentPath] as? Loaded)?.children
        return parentFiles?.remove(currentFile) ?: false
    }


    override fun toString(): String {
        return "FileListLoader(cacheFiles=$cacheFiles)"
    }

    sealed interface CacheEntry {
        fun getFiles(): List<File>
    }

    data class Loaded(val children: MutableList<File>) : CacheEntry {
        override fun getFiles(): List<File> = children
    }

    data class Loading(val deferred: Deferred<MutableList<File>>) : CacheEntry {
        override fun getFiles(): List<File> = emptyList()
    }

    object FileListLoaderSaver : Saver<FileListLoader, Map<String, List<JFile>>> {
        override fun restore(value: Map<String, List<JFile>>): FileListLoader {
            return FileListLoader(mutableMapOf<String, CacheEntry>().also { cache ->
                value.mapValuesTo(cache) { entry ->
                    Loaded(mutableListOf<File>().also { list ->
                        entry.value.mapTo(list) { it.wrapFile() }
                    })
                }
            })
        }

        override fun SaverScope.save(value: FileListLoader): Map<String, List<JFile>> {
            val res = mutableMapOf<String, List<JFile>>()
            value.cacheFiles.entries.forEach { entry ->
                val cached = (entry.value as? Loaded)?.children?.mapNotNull { it.asRawFile() }
                if (!cached.isNullOrEmpty()) {
                    res[entry.key] = cached
                }
            }
            return res
        }
    }
}