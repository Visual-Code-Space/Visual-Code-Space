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

package com.teixeira.vcspace.extensions

import android.util.Base64
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

fun String.toFile() = File(this)

fun File.toBase64String(): String = Base64.encodeToString(readBytes(), Base64.NO_WRAP)
fun String.decodeBase64(): ByteArray = Base64.decode(this, Base64.NO_WRAP)

fun File.toZipFile(): File {
    val zipFile = File(parent, "$name.zip")

    ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { zos ->
        walkTopDown().forEach { file ->
            val relativePath = file.relativeTo(this).path
            if (file.isDirectory) {
                if (relativePath.isNotEmpty()) {
                    zos.putNextEntry(ZipEntry("$relativePath/"))
                    zos.closeEntry()
                }
            } else {
                zos.putNextEntry(ZipEntry(relativePath))
                file.inputStream().use { it.copyTo(zos) }
                zos.closeEntry()
            }
        }
    }

    return zipFile
}

fun File.extractZipFile(destinationDir: File) {
    ZipInputStream(inputStream()).use { zipInputStream ->
        var entry: ZipEntry? = zipInputStream.nextEntry

        while (entry != null) {
            val extractedFile = File(destinationDir, entry.name)
            if (!extractedFile.canonicalPath.startsWith(destinationDir.canonicalPath)) {
                throw SecurityException("Bad zip entry path: ${entry.name}")
            }

            if (entry.isDirectory) {
                extractedFile.mkdirs()
            } else {
                extractedFile.parentFile?.mkdirs() // Ensure parent directories exist
                extractedFile.outputStream().use { output ->
                    zipInputStream.copyTo(output)
                }
            }

            zipInputStream.closeEntry()
            entry = zipInputStream.nextEntry
        }
    }
}

fun File.child(fileName: String): File {
    return File(this, fileName)
}

fun File.createFileIfNot(): File {
    if (exists().not()) {
        createNewFile()
    }
    return this
}
