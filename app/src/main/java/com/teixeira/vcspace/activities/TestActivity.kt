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
import io.github.rosemoe.sora.lsp.client.languageserver.wrapper.EventHandler
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
                
                // Configure editor for better LSP integration
                editor.apply {
                    nonPrintablePaintingFlags = CodeEditor.FLAG_DRAW_WHITESPACE_LEADING or 
                                               CodeEditor.FLAG_DRAW_LINE_SEPARATOR or
                                               CodeEditor.FLAG_DRAW_WHITESPACE_IN_SELECTION
                    getComponent<EditorAutoCompletion>().apply {
                        isEnabled = true
                        setMaxHeight(300)
                    }
                }
            }

            val intent = Intent(this@TestActivity, HtmlService::class.java)
            intent.putExtra("port", 2087)
            startService(intent)

            // Wait a moment for the service to start
            delay(500)

            val serverDefinition =
                object : CustomLanguageServerDefinition("html", ServerConnectProvider {
                    SocketStreamConnectionProvider(2087)
                }) {
                    override val eventListener: EventHandler.EventListener
                        get() = super.eventListener
                }

            lspProject = LspProject(filesDir.absolutePath)
            lspProject.addServerDefinition(serverDefinition)

            withContext(Dispatchers.Main) {
                val htmlFile = filesDir.resolve("index.html").absolutePath
                lspEditor = lspProject.createEditor(htmlFile)
                val lspLanguage = LspLanguage(lspEditor)
                lspEditor.wrapperLanguage = lspLanguage
                editor.setEditorLanguage(lspLanguage)
                lspEditor.editor = editor
            }

            // Give more time for connection
            delay(1000)

            var connected = false
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
            
            // Set text after LSP connection is established
            withContext(Dispatchers.Main) {
                editor.setText(file.readText(), null)
            }
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
        private var serverSocket: ServerSocket? = null
        private var running = true
        
        override fun onBind(intent: Intent): IBinder? {
            return null
        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            thread {
                val port = intent?.getIntExtra("port", 0) ?: 0
                serverSocket = ServerSocket(port)
                println("Server started on port $port")
                
                while (running) {
                    try {
                        val client = serverSocket?.accept() ?: break
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
                            println("Error in LSP connection: ${it.message}")
                            it.printStackTrace()
                        }

                        client.close()
                    } catch (e: Exception) {
                        if (running) {
                            println("Socket error: ${e.message}")
                            e.printStackTrace()
                        }
                        break
                    }
                }
                
                serverSocket?.close()
            }

            return START_STICKY
        }
        
        override fun onDestroy() {
            running = false
            serverSocket?.close()
            super.onDestroy()
        }
    }
}
