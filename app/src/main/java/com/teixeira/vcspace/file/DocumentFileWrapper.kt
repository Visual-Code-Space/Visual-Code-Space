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

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream

data class DocumentFileWrapper(
  private val raw: DocumentFile,
  private val isDocumentTree: Boolean = false,
): File {
  override val absolutePath: String
    get() = raw.uri.toString()
  override val canonicalPath: String
    get() = raw.uri.toString()
  override val canRestoreFromPath: Boolean
    get() = false
  private val _isDirectory = lazy { raw.isDirectory }
  override val isDirectory: Boolean
    get() = _isDirectory.value
  private val _isFile = lazy { raw.isFile }
  override val isFile: Boolean
    get() = _isFile.value
  /**
   * This needs to be consolidated with the same method as [JavaFileWrapper.isValidText]
   */
  override val isValidText: Boolean
    get() = true
  private val _name = lazy { raw.name ?: "(unknown)" }
  override val name: String
    get() = _name.value
  override val mimeType: String?
    get() = raw.type
  override val parent: String?
    get() = raw.parentFile?.uri?.toString()
  override val parentFile: File?
    get() = raw.parentFile?.let { DocumentFileWrapper(it) }
  override val path: String
    get() = raw.uri.path ?: "UNKNOWN"

  override fun asRawFile(): java.io.File? = null

  override fun childExists(childName: String): Boolean
    = raw.findFile(childName) != null

  override fun createNewFile(fileName: String): File?
    = raw.createFile("", fileName)?.let { DocumentFileWrapper(it) }

  override fun createNewDirectory(fileName: String): File?
    = raw.createDirectory(fileName)?.let { DocumentFileWrapper(it) }

  override fun delete(): Boolean = raw.delete()

  override fun exists(): Boolean = raw.exists()

  override fun lastModified(): Long = raw.lastModified()

  override fun listFiles(): Array<out File>
    = raw.listFiles().map { DocumentFileWrapper(it) }.toTypedArray()

  override fun renameTo(newName: String): File? =
    if (raw.renameTo(newName)) {
      this
    } else null

  override fun uri(context: Context): Uri = raw.uri

  override suspend fun readFile2String(context: Context): String {
    val inputStream = context.contentResolver.openInputStream(raw.uri)
    val reader = BufferedReader(InputStreamReader(inputStream))
    return reader.readLines().joinToString("\n")
  }

  override suspend fun write(
    context: Context,
    content: String,
    ioDispatcher: CoroutineDispatcher,
  ): Boolean = withContext(ioDispatcher) {
    var outputStream: OutputStream? = null
    try {
      outputStream = context.contentResolver.openOutputStream(raw.uri,"wt")
      val checkedOutputStream = outputStream ?: return@withContext false
      checkedOutputStream.write(content.toByteArray())
    } catch (t : Throwable) {
      return@withContext false
    } finally {
      outputStream?.close()
    }
    true
  }

  companion object {
    /**
     * true if [DocumentFileWrapper] should be used.
     *
     * The implementation is conservative as a lot of functionality stops working
     * when [DocumentFileWrapper] is used. The target implementation will accept all
     * content uris for custom [androidx.core.content.FileProvider]s.
     */
    fun shouldWrap(uri: Uri): Boolean
      = ContentResolver.SCHEME_CONTENT == uri.scheme && "com.termux.documents" == uri.host
  }
}