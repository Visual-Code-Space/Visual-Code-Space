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

package com.teixeira.vcspace.tasks

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

data class DownloadProgress(
    val downloadedBytes: Long,
    val totalBytes: Long,
    val progress: Float,
    val isCompleted: Boolean = false,
    val error: Throwable? = null
)

object Downloader {
    private const val DEFAULT_BUFFER_SIZE = 8192
    private const val TAG = "Downloader"

    fun download(
        url: String,
        outputFile: File,
        bufferSize: Int = DEFAULT_BUFFER_SIZE
    ): Flow<DownloadProgress> = flow {
        Log.d(TAG, "Starting download from URL: $url")
        Log.d(TAG, "Output file: ${outputFile.absolutePath}")

        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            connection.connect()
            Log.d(TAG, "Connection established")

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                val error =
                    "Server returned HTTP ${connection.responseCode} ${connection.responseMessage}"
                Log.e(TAG, error)
                throw IOException(error)
            }

            val totalBytes = connection.contentLength.toLong()
            var downloadedBytes = 0L
            Log.d(TAG, "Total bytes to download: $totalBytes")

            connection.inputStream.use { input ->
                outputFile.outputStream().use { output ->
                    val buffer = ByteArray(bufferSize)
                    var bytes = input.read(buffer)

                    while (bytes >= 0) {
                        output.write(buffer, 0, bytes)
                        downloadedBytes += bytes

                        val progress = downloadedBytes.toFloat() / totalBytes
                        Log.v(
                            TAG,
                            "Progress: ${progress * 100}% ($downloadedBytes/$totalBytes bytes)"
                        )

                        emit(
                            DownloadProgress(
                                downloadedBytes = downloadedBytes,
                                totalBytes = totalBytes,
                                progress = progress
                            )
                        )

                        bytes = input.read(buffer)
                    }
                }
            }

            Log.d(TAG, "Download completed successfully")
            emit(
                DownloadProgress(
                    downloadedBytes = downloadedBytes,
                    totalBytes = totalBytes,
                    progress = 1f,
                    isCompleted = true
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Download failed", e)
            emit(
                DownloadProgress(
                    downloadedBytes = 0,
                    totalBytes = 0,
                    progress = 0f,
                    isCompleted = false,
                    error = e
                )
            )
        } finally {
            connection.disconnect()
            Log.d(TAG, "Connection disconnected")
        }
    }.flowOn(Dispatchers.IO)
}
