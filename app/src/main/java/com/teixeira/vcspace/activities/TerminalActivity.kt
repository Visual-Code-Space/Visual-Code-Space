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

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ToastUtils
import com.teixeira.vcspace.app.strings
import com.teixeira.vcspace.extensions.child
import com.teixeira.vcspace.extensions.createFileIfNot
import com.teixeira.vcspace.extensions.localDir
import com.teixeira.vcspace.extensions.tmpDir
import com.teixeira.vcspace.terminal.Terminal
import com.teixeira.vcspace.terminal.alpineDir
import com.teixeira.vcspace.terminal.appDataDir
import com.teixeira.vcspace.terminal.hosts
import com.teixeira.vcspace.terminal.nameserver
import com.teixeira.vcspace.terminal.prefix
import com.teixeira.vcspace.terminal.service.TerminalService
import com.teixeira.vcspace.ui.theme.VCSpaceTheme
import com.termux.view.TerminalView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.nio.file.Files

class TerminalActivity : ComponentActivity() {
    var terminalBinder: TerminalService.TerminalBinder? = null
    var isBound by mutableStateOf(false)

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            terminalBinder = service as TerminalService.TerminalBinder
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            terminalBinder = null
            isBound = false
        }
    }

    val workingDirectory: String
        get() {
            val extras = intent.extras
            if (extras != null && extras.containsKey(KEY_WORKING_DIRECTORY)) {
                val directory = extras.getString(KEY_WORKING_DIRECTORY, null)
                return if (directory != null && directory.trim().isNotEmpty()) directory
                else PathUtils.getRootPathExternalFirst()
            }
            return PathUtils.getRootPathExternalFirst()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VCSpaceTheme {
                Surface {
                    MainScreen()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, TerminalService::class.java).also { intent ->
            bindService(
                intent,
                serviceConnection,
                Context.BIND_AUTO_CREATE
            )
        }
    }

    override fun onStop() {
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
        super.onStop()
    }

    @Composable
    fun MainScreen() {
        var progress by remember { mutableFloatStateOf(0f) }
        var progressText by remember { mutableStateOf("Initializing...") }
        var isSetupComplete by remember { mutableStateOf(false) }
        var needsDownload by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            try {
                val abi = Build.SUPPORTED_ABIS

                val filesToDownload = listOf(
                    DownloadFile(
                        url = if (abi.contains("x86_64")) {
                            x86_64_packages
                        } else if (abi.contains("arm64-v8a")) {
                            aarch64_packages
                        } else if (abi.contains("armeabi-v7a")) {
                            arm_packages
                        } else {
                            throw RuntimeException("Unsupported CPU")
                        }, outputPath = "tmp/usr.tar.gz"
                    )
                ).toMutableList()

                if (alpineDir.listFiles().isNullOrEmpty()) {
                    filesToDownload.add(
                        DownloadFile(
                            url = if (abi.contains("x86_64")) {
                                alpine_x86_64
                            } else if (abi.contains("arm64-v8a")) {
                                alpine_aarch64
                            } else if (abi.contains("armeabi-v7a")) {
                                alpine_arm
                            } else {
                                throw RuntimeException("Unsupported CPU")
                            }, outputPath = "tmp/alpine.tar.gz"
                        )
                    )

                }

                needsDownload = filesToDownload.any { file ->
                    !File(filesDir.parentFile, file.outputPath).exists()
                }

                setupEnvironment(
                    context = this@TerminalActivity,
                    filesToDownload = filesToDownload,
                    onProgress = { completedFiles, totalFiles, currentProgress ->
                        if (needsDownload) {
                            val fileProgress = completedFiles.toFloat() / totalFiles.toFloat()
                            val combinedProgress = (fileProgress + currentProgress) / totalFiles
                            progress = combinedProgress.coerceIn(
                                0f, 1f
                            )
                            progressText = "Downloading... ${(progress * 100).toInt()}%"
                        }
                    },
                    onComplete = {
                        isSetupComplete = true
                    },
                    onError = { error ->
                        error.printStackTrace()
                        ToastUtils.showShort("Setup Failed: ${error.message}")
                        finish()
                    }
                )
            } catch (e: Exception) {
                e.printStackTrace()
                ToastUtils.showShort("Setup Failed: ${e.message}")
                finish()
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            if (!isSetupComplete) {
                if (needsDownload) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = progressText, style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth(0.8f),
                        )
                    }
                }
            } else {
                Terminal(terminalActivity = this@TerminalActivity)
            }
        }
    }

    fun compilePython(terminal: TerminalView) {
        var filePath = intent?.extras?.getString(KEY_PYTHON_FILE_PATH, null) ?: return

        if (filePath.contains(" ")) {
            filePath = "'$filePath'"
        }

        if (filePath.isNotEmpty()) {
            val message = "\\033[32;49;1mCompiling\\033[0m $filePath\n"
            ThreadUtils.getMainHandler().post {
                terminal.mTermSession.write(
                    if (alpineDir.resolve("usr/bin/python").exists()) {
                        "clear && python $filePath && exit\r"
                    } else {
                        "clear && apk add python3 && clear && python $filePath && exit\r"
                    }
                )
            }
        }
    }

    data class DownloadFile(
        val url: String, val outputPath: String
    )

    private suspend fun setupEnvironment(
        context: Context,
        filesToDownload: List<DownloadFile>,
        onProgress: (completedFiles: Int, totalFiles: Int, currentProgress: Float) -> Unit,
        onComplete: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                var completedFiles = 0
                val totalFiles = filesToDownload.size
                var totalProgress = 0f

                filesToDownload.forEach { file ->
                    val outputFile = File(context.filesDir.parentFile, file.outputPath)

                    outputFile.parentFile?.mkdirs()

                    if (!outputFile.exists()) {
                        outputFile.createNewFile()

                        downloadFile(
                            url = file.url,
                            outputFile = outputFile,
                            onProgress = { downloadedBytes, totalBytes ->
                                val currentFileProgress =
                                    downloadedBytes.toFloat() / totalBytes.toFloat()
                                totalProgress = (completedFiles + currentFileProgress) / totalFiles

                                runOnUiThread {
                                    onProgress(completedFiles, totalFiles, totalProgress)
                                }
                            }
                        )
                    }
                    completedFiles++
                    withContext(Dispatchers.Main) {
                        onProgress(completedFiles, totalFiles, totalProgress)
                    }

                    runCatching {
                        outputFile.setExecutable(true)
                    }.onFailure { it.printStackTrace() }
                }

                makeRootFs {
                    extractPackage { runOnUiThread { onComplete() } }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                localDir.deleteRecursively()
                withContext(Dispatchers.Main) {
                    onError(e)
                }
            }
        }
    }

    private fun extractPackage(onComplete: () -> Unit) {
        val usr = File(tmpDir, "usr.tar.gz")

        if (usr.exists().not() || prefix.listFiles().isNullOrEmpty().not()) {
            onComplete()
            return
        }

        Runtime.getRuntime().exec("tar -xf ${usr.absolutePath} -C $appDataDir").waitFor()
        usr.delete()

        val libtallocSo2Path = File(prefix, "lib/libtalloc.so.2").apply {
            if (exists()) delete()
        }.toPath()
        val libtallocSo241Path = File(prefix, "lib/libtalloc.so.2.4.1").toPath()
        Files.createSymbolicLink(libtallocSo2Path, libtallocSo241Path)
        onComplete()
    }

    private fun makeRootFs(onComplete: () -> Unit) {
        val alpine = File(tmpDir, "alpine.tar.gz")

        if (alpine.exists().not() || alpineDir.listFiles().isNullOrEmpty().not()) {
            onComplete()
        } else {
            Runtime.getRuntime().exec("tar -xf ${alpine.absolutePath} -C $alpineDir").waitFor()
            alpine.delete()
            with(alpineDir) {
                child("etc/hostname").writeText(getString(strings.app_name))
                child("etc/resolv.conf").also { it.createFileIfNot();it.writeText(nameserver) }
                child("etc/hosts").writeText(hosts)
            }
            onComplete()
        }
    }

    private suspend fun downloadFile(
        url: String,
        outputFile: File,
        onProgress: (downloadedBytes: Long, totalBytes: Long) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            val client = OkHttpClient.Builder().build()
            val request = Request.Builder().url(url).build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw Exception("Failed to download file: ${response.code}")
                }

                val body = response.body ?: throw Exception("Empty response body")
                val totalBytes = body.contentLength()

                var downloadedBytes = 0L

                outputFile.outputStream().use { output ->
                    body.byteStream().use { input ->
                        val buffer = ByteArray(8 * 1024)
                        var bytesRead: Int

                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            downloadedBytes += bytesRead
                            withContext(Dispatchers.Main) {
                                onProgress(downloadedBytes, totalBytes)
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val KEY_WORKING_DIRECTORY = "terminal_workingDirectory"
        const val KEY_PYTHON_FILE_PATH = "terminal_python_file"
    }
}

private const val aarch64_packages =
    "https://github.com/itsvks19/vcspace-packages/raw/refs/heads/main/aarch64/usr.tar.gz"
private const val arm_packages =
    "https://github.com/itsvks19/vcspace-packages/raw/refs/heads/main/arm/usr.tar.gz"
private const val x86_64_packages =
    "https://github.com/itsvks19/vcspace-packages/raw/refs/heads/main/x86_64/usr.tar.gz"

private const val alpine_arm =
    "https://dl-cdn.alpinelinux.org/alpine/v3.22/releases/armhf/alpine-minirootfs-3.22.1-armhf.tar.gz"
private const val alpine_aarch64 =
    "https://dl-cdn.alpinelinux.org/alpine/v3.22/releases/aarch64/alpine-minirootfs-3.22.1-aarch64.tar.gz"
private const val alpine_x86_64 =
    "https://dl-cdn.alpinelinux.org/alpine/v3.22/releases/x86_64/alpine-minirootfs-3.22.1-x86_64.tar.gz"
