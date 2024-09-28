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

package com.teixeira.vcspace.editor.lsp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.teixeira.vcspace.editor.lsp.KotlinLSPClient
import org.eclipse.lsp4j.jsonrpc.Launcher
import org.javacs.kt.KotlinLanguageServer
import java.net.ServerSocket
import kotlin.concurrent.thread

class KotlinLSPService : Service() {
  companion object {
    private const val TAG = "KotlinLSPService"
  }

  override fun onBind(intent: Intent?): IBinder? {
    return null
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    thread {
      val port = intent?.getIntExtra("port", 0) ?: 0
      val socket = ServerSocket(port)
      Log.d(TAG, "Starting socket on port ${socket.localPort}")

      val socketClient = socket.accept()
      Log.d(TAG, "connected to client on port ${socketClient.port}")

      runCatching {
        val server = KotlinLanguageServer()
        val inputStream = socketClient.getInputStream()
        val outputStream = socketClient.getOutputStream()

        val launcher = Launcher.createLauncher(
          server,
          KotlinLSPClient::class.java,
          inputStream,
          outputStream
        )

        server.connect(launcher.remoteProxy)
        launcher.startListening().get()
      }.onFailure {
        Log.e(TAG, "Error starting language server", it)
      }

      socketClient.close()
      socket.close()
    }

    return START_STICKY
  }
}