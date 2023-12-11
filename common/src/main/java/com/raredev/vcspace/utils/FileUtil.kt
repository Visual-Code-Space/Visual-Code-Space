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
package com.raredev.vcspace.utils

import android.content.Context
import java.io.ByteArrayOutputStream
import java.io.File

object FileUtil {
  fun isValidTextFile(filename: String): Boolean {
    return !filename.matches(
      ".*\\.(bin|ttf|png|jpe?g|bmp|mp4|mp3|m4a|iso|so|zip|rar|jar|dex|odex|vdex|7z|apk|apks|xapk)$".toRegex()
    )
  }

  fun getParentPath(path: String): String? {
    val index = path.lastIndexOf("/")
    return if (index != -1) {
      path.substring(0, index)
    } else null
  }

  fun delete(path: String?): Boolean {
    return delete(File(path.toString()))
  }

  fun delete(file: File): Boolean {
    if (!file.exists()) return false
    if (file.isFile()) {
      return file.delete()
    }
    val fileArr = file.listFiles()
    if (fileArr != null) {
      for (subFile in fileArr) {
        if (subFile.isDirectory()) {
          delete(subFile)
        }
        if (subFile.isFile()) {
          subFile.delete()
        }
      }
    }
    return file.delete()
  }

  fun readFromAsset(ctx: Context, path: String?): String {
    try {
      // Get the input stream from the asset
      val inputStream = ctx.assets.open(path!!)

      // Create a byte array output stream to store the read bytes
      val outputStream = ByteArrayOutputStream()

      // Create a buffer of 1024 bytes
      val _buf = ByteArray(1024)
      var i: Int

      // Read the bytes from the input stream, write them to the output stream and close the streams
      while (inputStream.read(_buf).also { i = it } != -1) {
        outputStream.write(_buf, 0, i)
      }
      outputStream.close()
      inputStream.close()

      // Return the content of the output stream as a String
      return outputStream.toString()
    } catch (e: Exception) {
      e.printStackTrace()
    }

    // If an exception occurred, return an empty String
    return ""
  }
}
