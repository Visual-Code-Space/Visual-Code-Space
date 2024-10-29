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

package com.teixeira.vcspace.webserver

import fi.iki.elonen.NanoHTTPD
import java.io.File
import java.io.IOException

class LocalHttpServer(
  private val directory: String,
  port: Int = 0
) : NanoHTTPD(port) {
  val assignedPort: Int
    get() = listeningPort

  override fun serve(session: IHTTPSession): Response {
    val requestedUri = session.uri
    val file = if (requestedUri == "/") {
      File(directory, "index.html").normalize()
    } else {
      File(directory, requestedUri).normalize()
    }

    if (
      !file.exists() ||
      !file.isFile ||
      !file.canonicalPath.startsWith(File(directory).canonicalPath)
    ) {
      return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "404 Not Found")
    }

    return try {
      val mimeType = getMimeTypeForFile(file.absolutePath)
      newFixedLengthResponse(Response.Status.OK, mimeType, file.inputStream(), file.length())
    } catch (e: IOException) {
      newFixedLengthResponse(
        Response.Status.INTERNAL_ERROR,
        MIME_PLAINTEXT,
        "500 Internal Server Error"
      )
    }
  }
}