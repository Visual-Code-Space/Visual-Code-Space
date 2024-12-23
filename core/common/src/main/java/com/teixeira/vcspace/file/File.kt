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

package com.teixeira.vcspace.file

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.io.File as JFile

/**
 * Consolidated interface that blends multiple File access formats.
 */
interface File {
  val absolutePath: String
  val canonicalPath: String
  /**
   * true if this file can be restored from path
   */
  val canRestoreFromPath: Boolean
  val isDirectory: Boolean
  val isFile: Boolean
  val isValidText: Boolean
  val name: String
  val mimeType: String?
  val parent: String?
  val parentFile: File?
  val path: String

  fun asRawFile(): JFile?
  fun childExists(childName: String): Boolean
  fun createNewFile(fileName: String): File?
  fun createNewDirectory(fileName: String): File?
  fun delete(): Boolean
  fun exists(): Boolean
  fun lastModified(): Long
  fun listFiles(): Array<out File>?
  fun renameTo(newName: String): File?
  fun uri(context: Context): Uri
  suspend fun readFile2String(context: Context): String?
  suspend fun write(
    context: Context,
    content: String,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): Boolean
}

val File.extension: String
  get() = name.substringAfterLast('.', "")
