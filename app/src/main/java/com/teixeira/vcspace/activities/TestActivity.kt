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

package com.teixeira.vcspace.activities

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.Typeface
import android.os.IBinder
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.teixeira.vcspace.activities.base.BaseComposeActivity
import com.teixeira.vcspace.lsp.html.HtmlLanguageServer
import io.github.rosemoe.sora.lsp.client.connection.SocketStreamConnectionProvider
import io.github.rosemoe.sora.lsp.client.languageserver.serverdefinition.CustomLanguageServerDefinition
import io.github.rosemoe.sora.lsp.client.languageserver.serverdefinition.CustomLanguageServerDefinition.ServerConnectProvider
import io.github.rosemoe.sora.lsp.editor.LspEditor
import io.github.rosemoe.sora.lsp.editor.LspLanguage
import io.github.rosemoe.sora.lsp.editor.LspProject
import io.github.rosemoe.sora.lsp.requests.Timeout
import io.github.rosemoe.sora.lsp.requests.Timeouts
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion
import io.github.rosemoe.sora.widget.component.EditorTextActionWindow
import io.github.rosemoe.sora.widget.getComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.eclipse.lsp4j.launch.LSPLauncher
import java.net.ServerSocket
import kotlin.concurrent.thread

class TestActivity : BaseComposeActivity() {
    @SuppressLint("SetJavaScriptEnabled", "MaterialDesignInsteadOrbitDesign")
    @Composable
    override fun MainScreen() {
        val editor = remember { CodeEditor(this) }
        lateinit var lspEditor: LspEditor
        lateinit var lspProject: LspProject

        suspend fun connectToLsp() = withContext(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                println("Starting Language Server...")
                editor.editable = false
            }

            val intent = Intent(this@TestActivity, HtmlService::class.java)
            intent.putExtra("port", 2087)
            startService(intent)

            val serverDefinition =
                object : CustomLanguageServerDefinition("html", ServerConnectProvider {
                    SocketStreamConnectionProvider(2087)
                }) {

                }

            lspProject = LspProject(filesDir.absolutePath)
            lspProject.addServerDefinition(serverDefinition)

            withContext(Dispatchers.Main) {
                lspEditor = lspProject.createEditor(filesDir.resolve("index.html").absolutePath)
                lspEditor.wrapperLanguage = LspLanguage(lspEditor)
                lspEditor.editor = editor
            }

            var connected: Boolean
            delay(Timeout[Timeouts.INIT].toLong())

            try {
                lspEditor.connectWithTimeout()
                connected = true
            } catch (e: Exception) {
                connected = false
                e.printStackTrace()
            }

            if (connected) {
                println("Connected to language server")
            } else {
                println("Failed to connect to language server")
            }

            withContext(Dispatchers.Main) {
                editor.editable = true
            }
        }

        LaunchedEffect(Unit) {
            val file = filesDir.resolve("index.html")
            file.writeText(
                """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Test</title>
                </head>
                <body>
                    
                </body>
                </html>
            """.trimIndent()
            )

            connectToLsp()
            editor.setText(file.readText(), null)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = {
                    editor.apply {
                        val font =
                            Typeface.createFromAsset(assets, "fonts/JetBrainsMono-Regular.ttf")
                        typefaceText = font
                        typefaceLineNumber = font
                        getComponent<EditorTextActionWindow>().isEnabled = true
                        getComponent<EditorAutoCompletion>().isEnabled = true
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    class HtmlService : Service() {
        override fun onBind(intent: Intent): IBinder? {
            return null
        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            thread {
                val port = intent?.getIntExtra("port", 0) ?: 0
                val socket = ServerSocket(port)
                println("Server started on port $port")
                val client = socket.accept()
                println("Client connected: ${client.inetAddress.hostAddress}")

                runCatching {
                    val inputStream = client.getInputStream()
                    val outputStream = client.getOutputStream()

                    val server = HtmlLanguageServer()

                    val launcher = LSPLauncher.createServerLauncher(server, inputStream, outputStream)
                    val languageClient = launcher.remoteProxy
                    server.connect(languageClient)

                    val startListening = launcher.startListening()
                    startListening.get()
                }.onFailure {
                    println("Error: ${it.message}")
                    it.printStackTrace()
                }

                client.close()
                socket.close()
            }

            return START_STICKY
        }
    }
}
