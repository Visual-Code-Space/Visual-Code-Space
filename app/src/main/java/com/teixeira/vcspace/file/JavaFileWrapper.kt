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
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.blankj.utilcode.util.FileIOUtils
import com.teixeira.vcspace.utils.isValidTextFile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

internal data class JavaFileWrapper(
  private val raw: java.io.File
) : File {
  override val absolutePath: String
    get() = raw.absolutePath
  override val canonicalPath: String
    get() = raw.canonicalPath
  override val isDirectory: Boolean
    get() = raw.isDirectory
  override val isFile: Boolean
    get() = raw.isFile
  override val isValidText: Boolean
    get() = isValidTextFile(raw)
  override val name: String
    get() = raw.name
  override val mimeType: String
    get() {
      val lastDot = name.lastIndexOf('.')
      if (lastDot >= 0) {
        val extension = name.substring(lastDot + 1).lowercase(Locale.getDefault())
        val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        if (mime != null) {
          return mime
        }
      }
      return "application/octet-stream"
    }
  override val parent: String?
    get() = raw.parent
  override val parentFile: File?
    get() = raw.parentFile?.let { JavaFileWrapper(it) }
  override val path: String
    get() = raw.path

  override fun asRawFile(): java.io.File = raw

  override fun childExists(childName: String): Boolean = java.io.File(raw, childName).exists()

  override fun createNewFile(fileName: String): File? {
    val target = java.io.File(raw, fileName)
    try {
      target.createNewFile()
      return JavaFileWrapper(target)
    } catch (e: IOException) {
      return null
    }
  }

  override fun createNewDirectory(fileName: String): File? {
    val target = java.io.File(raw, fileName)
    try {
      target.mkdirs()
      return JavaFileWrapper(target)
    } catch (e: IOException) {
      return null
    }
  }
  override fun delete(): Boolean = raw.delete()

  override fun exists(): Boolean = raw.exists()

  override fun lastModified(): Long =
    raw.lastModified()

  override fun listFiles(): Array<out File>?
    = raw.listFiles()?.map { JavaFileWrapper(it) }?.toTypedArray()

  override fun renameTo(newName: String): File? {
    val dest = java.io.File(raw.parentFile, newName)
    return if (raw.renameTo(dest)) JavaFileWrapper(dest) else null
  }

  override fun uri(context: Context): Uri =
    FileProvider.getUriForFile(
      context,
      "$context.packageName.provider",
      raw
    )

  override suspend fun readFile2String(context: Context): String?
    = FileIOUtils.readFile2String(raw)

  override suspend fun write(
    context: Context,
    content: String,
    ioDispatcher: CoroutineDispatcher,
  ): Boolean = withContext(ioDispatcher) {
    FileIOUtils.writeFileFromString(raw, content)
  }
}

/**
 * Wrap [java.io.File] with [JavaFileWrapper]
 */
fun java.io.File.wrapFile(): File = JavaFileWrapper(this)