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

package com.teixeira.vcspace.ui.screens.editor.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Process
import android.util.Log
import android.view.View
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Redo
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.KeyboardCommandKey
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.UriUtils
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.google.android.material.snackbar.Snackbar
import com.hzy.libp7zip.P7ZipApi
import com.teixeira.vcspace.PYTHON_PACKAGE_URL_32_BIT
import com.teixeira.vcspace.PYTHON_PACKAGE_URL_64_BIT
import com.teixeira.vcspace.activities.Editor.LocalCommandPaletteManager
import com.teixeira.vcspace.activities.Editor.LocalEditorDrawerState
import com.teixeira.vcspace.activities.MarkdownPreviewActivity
import com.teixeira.vcspace.activities.TerminalActivity
import com.teixeira.vcspace.app.strings
import com.teixeira.vcspace.compose.LocalMenuManager
import com.teixeira.vcspace.core.components.Tooltip
import com.teixeira.vcspace.core.components.common.VCSpaceTopBar
import com.teixeira.vcspace.core.settings.Settings.EditorTabs.rememberAutoSave
import com.teixeira.vcspace.editor.events.OnContentChangeEvent
import com.teixeira.vcspace.editor.events.OnKeyBindingEvent
import com.teixeira.vcspace.extensions.open
import com.teixeira.vcspace.file.extension
import com.teixeira.vcspace.file.wrapFile
import com.teixeira.vcspace.keyboard.model.Command.Companion.newCommand
import com.teixeira.vcspace.preferences.pythonDownloaded
import com.teixeira.vcspace.preferences.pythonExtracted
import com.teixeira.vcspace.resources.R
import com.teixeira.vcspace.ui.screens.editor.EditorViewModel
import com.teixeira.vcspace.ui.screens.editor.components.view.CodeEditorView
import com.teixeira.vcspace.utils.isFileRunnable
import com.teixeira.vcspace.utils.launchWithProgressDialog
import com.teixeira.vcspace.webserver.LocalHttpServer
import io.github.rosemoe.sora.event.ContentChangeEvent
import io.github.rosemoe.sora.event.KeyBindingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.io.File as JFile

@Composable
fun EditorTopBar(
    modifier: Modifier = Modifier,
    editorViewModel: EditorViewModel
) {
    val scope = rememberCoroutineScope()
    val drawerState = LocalEditorDrawerState.current
    val commandPaletteManager = LocalCommandPaletteManager.current
    val menuManager = LocalMenuManager.current

    var showMenu by remember { mutableStateOf(false) }
    val showFileMenu = remember { mutableStateOf(false) }

    val editors = editorViewModel.editors
    val monacoEditors = editorViewModel.monacoEditors
    val uiState by editorViewModel.uiState.collectAsStateWithLifecycle()

    val selectedFileIndex = uiState.selectedFileIndex
    val selectedFile = uiState.openedFiles.getOrNull(selectedFileIndex)

    val selectedEditor = selectedFile?.let { editors[it.file.path] }
    val selectedMonacoEditor = selectedFile?.let { monacoEditors[it.file.path] }

    var canUndo by remember { mutableStateOf(false) }
    var canRedo by remember { mutableStateOf(false) }

    val areModifiedFiles by remember(selectedFileIndex) {
        derivedStateOf {
            editors.any { it.value.modified }
        }
    }

    val autoSave by rememberAutoSave()

    LaunchedEffect(selectedEditor, selectedMonacoEditor, autoSave) {
        selectedEditor?.let { editorView ->
            canUndo = editorView.canUndo()
            canRedo = editorView.canRedo()

            editorView.editor.subscribeEvent(ContentChangeEvent::class.java) { event, _ ->
                when (event.action) {
                    ContentChangeEvent.ACTION_SET_NEW_TEXT,
                    ContentChangeEvent.ACTION_INSERT,
                    ContentChangeEvent.ACTION_DELETE -> {
                        //editorView.editor.getComponent(EditorAutoCompletion::class.java).requireCompletion()
                    }
                }

                EventBus.getDefault().post(OnContentChangeEvent(selectedFile.file))
                editorView.setModified(event.action != ContentChangeEvent.ACTION_SET_NEW_TEXT)
                editorViewModel.setModified(selectedFile.file, editorView.modified)
                canUndo = editorView.canUndo()
                canRedo = editorView.canRedo()

                if (autoSave) {
                    scope.launch {
                        delay(100)
                        editorViewModel.saveFile()
                    }
                }
            }

            editorView.editor.subscribeEvent(KeyBindingEvent::class.java) { event, _ ->
                EventBus.getDefault().post(OnKeyBindingEvent(event.canEditorHandle()))
            }
        }

        selectedMonacoEditor?.let { editor ->
            canUndo = editor.canUndo
            canRedo = editor.canRedo

            editor.onContentChange = {
                EventBus.getDefault().post(OnContentChangeEvent(selectedFile.file))
                editorViewModel.setModified(selectedFile.file, true)
                canUndo = editor.canUndo
                canRedo = editor.canRedo

                if (autoSave) {
                    scope.launch {
                        delay(100)
                        editorViewModel.saveFile()
                    }
                }
            }
        }
    }

    val context = LocalContext.current
    val view = LocalView.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isKeyboardOpen by remember { mutableStateOf(KeyboardUtils.isSoftInputVisible(context as Activity)) }

    LifecycleStartEffect(key1 = lifecycleOwner) {
        KeyboardUtils.registerSoftInputChangedListener(context as Activity) {
            isKeyboardOpen = KeyboardUtils.isSoftInputVisible(context)
        }

        onStopOrDispose {
            KeyboardUtils.unregisterSoftInputChangedListener(context.window)
        }
    }

    var server: LocalHttpServer? = null

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                server?.let {
                    if (it.isAlive || it.wasStarted()) {
                        it.closeAllConnections()
                        it.stop()
                    }
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    VCSpaceTopBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(id = strings.app_name),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        },
        navigationIcon = {
            Tooltip(stringResource(id = strings.open_drawer)) {
                IconButton(onClick = {
                    scope.launch {
                        drawerState.apply {
                            if (isOpen) close() else open()
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Menu,
                        contentDescription = stringResource(id = strings.open_drawer)
                    )
                }
            }
        },
        actions = {
            AnimatedVisibility(
                visible = isFileRunnable(selectedFile?.file)
            ) {
                Tooltip(stringResource(id = strings.execute)) {
                    IconButton(
                        onClick = {
                            when (selectedFile?.file?.extension) {
                                "html", "htm" -> {
                                    selectedFile.file.parent?.let { directory ->
                                        server?.let {
                                            if (it.isAlive || it.wasStarted()) {
                                                it.closeAllConnections()
                                                it.stop()
                                            }
                                        }

                                        server = LocalHttpServer(directory)

                                        runCatching {
                                            server?.start()
                                            val assignedPort = server?.assignedPort ?: 8000
                                            ToastUtils.showLong("Server started on http://localhost:$assignedPort")

                                            val customTabs = CustomTabsIntent.Builder()
                                                .setShowTitle(true)
                                                .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
                                                .build()

                                            customTabs.launchUrl(
                                                context,
                                                "http://localhost:$assignedPort/${selectedFile.file.name}".toUri()
                                            )
                                        }.onFailure {
                                            Log.e(
                                                "ServerError",
                                                "Failed to start server: ${it.message}"
                                            )
                                            ToastUtils.showLong(
                                                context.getString(
                                                    R.string.failed_to_start_server,
                                                    it.message
                                                )
                                            )
                                        }
                                    }
                                }

                                "py" -> downloadPythonPackage(
                                    scope = scope,
                                    context = context,
                                    view = view
                                ) {
                                    context.startActivity(
                                        Intent(context, TerminalActivity::class.java).apply {
                                            putExtra(
                                                TerminalActivity.KEY_PYTHON_FILE_PATH,
                                                selectedFile.file.absolutePath
                                            )
                                        }
                                    )
                                }

                                "md" -> {
                                    context.startActivity(
                                        Intent(
                                            context,
                                            MarkdownPreviewActivity::class.java
                                        ).apply {
                                            putExtra(
                                                MarkdownPreviewActivity.EXTRA_FILE_PATH,
                                                selectedFile.file.absolutePath
                                            )
                                        })
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = null
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = isKeyboardOpen
            ) {
                Row {
                    Tooltip(stringResource(id = strings.editor_undo)) {
                        IconButton(
                            onClick = { selectedEditor?.undo() ?: selectedMonacoEditor?.undo() },
                            enabled = canUndo
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Undo,
                                contentDescription = null
                            )
                        }
                    }

                    Tooltip(stringResource(id = strings.editor_redo)) {
                        IconButton(
                            onClick = { selectedEditor?.redo() ?: selectedMonacoEditor?.redo() },
                            enabled = canRedo
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Redo,
                                contentDescription = null
                            )
                        }
                    }
                }
            }

            LaunchedEffect(Unit) {
                commandPaletteManager.addCommand(
                    newCommand("Terminal", "Ctrl+T") {
                        context.open(TerminalActivity::class.java)
                    },
                    newCommand("Search", "Ctrl+K") {
                        selectedEditor?.beginSearchMode()
                    }
                )
            }

            Box {
                Tooltip("Menu") {
                    IconButton(
                        onClick = { showMenu = !showMenu }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = null
                        )
                    }
                }

                DropdownMenu(
                    expanded = showMenu,
                    offset = DpOffset((-5).dp, 0.dp),
                    shape = MaterialTheme.shapes.medium,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(id = strings.editor_search)) },
                        leadingIcon = {
                            Icon(
                                Icons.Rounded.Search,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            Text("Ctrl+K")
                        },
                        enabled = selectedEditor != null,
                        onClick = {
                            selectedEditor?.beginSearchMode()
                            showMenu = false
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Command Palette") },
                        onClick = {
                            commandPaletteManager.show()
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Rounded.KeyboardCommandKey,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            Text("Ctrl+Shift+P")
                        }
                    )

                    DropdownMenuItem(
                        text = { Text(stringResource(id = strings.file)) },
                        leadingIcon = {
                            Icon(
                                Icons.Rounded.Folder,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            Icon(
                                Icons.Rounded.ChevronRight,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            showFileMenu.value = !showFileMenu.value
                            showMenu = false
                        }
                    )

                    menuManager.menuItems.forEach { menu ->
                        AnimatedVisibility(menu.visible) {
                            DropdownMenuItem(
                                text = { Text(menu.title) },
                                onClick = {
                                    menu.onClick()
                                    showMenu = false
                                },
                                leadingIcon = menu.icon?.let {
                                    { Icon(it, contentDescription = null) }
                                },
                                enabled = menu.enabled,
                                trailingIcon = menu.shortcut?.let {
                                    { Text(it) }
                                } ?: menu.trailingIcon?.let {
                                    { Icon(it, contentDescription = null) }
                                }
                            )
                        }
                    }
                }

                FileMenu(
                    showFileMenu = showFileMenu,
                    editorViewModel = editorViewModel,
                    editor = selectedEditor,
                    scope = scope,
                    modified = selectedFile?.isModified == true,
                    areModifiedFiles = areModifiedFiles
                )
            }
        }
    )
}

@Composable
fun FileMenu(
    showFileMenu: MutableState<Boolean>,
    editorViewModel: EditorViewModel,
    editor: CodeEditorView?,
    scope: CoroutineScope,
    modified: Boolean,
    areModifiedFiles: Boolean
) {
    val createFile = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/*")
    ) {
        if (it != null) editorViewModel.addFile(UriUtils.uri2File(it).wrapFile())
    }

    val openFile = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        if (it != null) editorViewModel.addFile(UriUtils.uri2File(it).wrapFile())
    }

    val context = LocalContext.current
    val commandPaletteManager = LocalCommandPaletteManager.current
    val menuManager = LocalMenuManager.current

    val launchFileCreate = remember {
        { createFile.launch("filename.txt") }
    }

    val launchOpenFile = remember {
        {
            openFile.launch(
                arrayOf(
                    "text/*",
                    "application/octet-stream",
                    "application/javascript",
                    "application/json",
                    "application/xml",
                )
            )
        }
    }

    val saveFile = remember {
        {
            scope.launch {
                editorViewModel.saveFile()
            }
        }
    }

    val saveAll = remember {
        {
            scope.launch {
                editorViewModel.saveAll()
            }
        }
    }

    LaunchedEffect(Unit) {
        commandPaletteManager.addCommand(
            newCommand("New File", "Ctrl+N") { launchFileCreate() },
            newCommand("Open File", "Ctrl+O") { launchOpenFile() },
            newCommand("Save File", "Ctrl+S") { saveFile() },
            newCommand("Save All Files", "Ctrl+Shift+S") { saveAll() }
        )

        menuManager.loadDefaultFileMenu(context) {
            when (it.id) {
                0 -> launchFileCreate()
                1 -> launchOpenFile()
                2 -> saveFile()
                3 -> {}
                4 -> saveAll()
                5 -> editor?.confirmReload()
            }
        }
    }

    DropdownMenu(
        shape = MaterialTheme.shapes.medium,
        expanded = showFileMenu.value,
        offset = DpOffset((-5).dp, 0.dp),
        onDismissRequest = { showFileMenu.value = false }
    ) {
        menuManager.fileMenuItems.forEach { menu ->
            menu.enabled = when (menu.id) {
                2 -> modified
                3 -> false
                4 -> areModifiedFiles
                5 -> editor != null
                else -> true
            }

            DropdownMenuItem(
                text = { Text(menu.title) },
                leadingIcon = menu.icon?.let {
                    { Icon(it, contentDescription = null) }
                },
                trailingIcon = menu.shortcut?.let {
                    { Text(it) }
                },
                enabled = menu.enabled,
                onClick = {
                    menu.onClick()
                    showFileMenu.value = false
                }
            )
        }
    }
}

private fun extractPythonFile(
    scope: CoroutineScope,
    context: Context,
    filePath: String,
    onDone: Runnable
) {
    if (pythonDownloaded) {
        onDone.run()
    } else {
        scope.launchWithProgressDialog(
            uiContext = context,
            context = Dispatchers.IO,
            configureBuilder = {
                it.setMessage(strings.python_extracting_python_compiler)
                    .setCancelable(false)
            },
            invokeOnCompletion = { throwable ->
                if (throwable == null) {
                    pythonDownloaded = true
                    onDone.run()
                }
            }
        ) { _, _ ->
            JFile(filePath).inputStream().use { temp7zStream ->
                val file =
                    JFile("${context.filesDir.absolutePath}/python.7z").apply { createNewFile() }
                Files.copy(temp7zStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
                val exitCode =
                    P7ZipApi.executeCommand("7z x ${file.absolutePath} -o${context.filesDir.absolutePath}")
                Log.d("EditorActivity", "extractFiles: $exitCode")
                file.delete()
            }
        }
    }
}

private fun downloadPythonPackage(
    scope: CoroutineScope,
    context: Context,
    view: View,
    onDownloaded: () -> Unit
) {
    if (pythonDownloaded) {
        onDownloaded()
        return
    }

    if (pythonExtracted) {
        FileUtils.deleteAllInDir(context.filesDir)
    }

    val url = if (Process.is64Bit()) PYTHON_PACKAGE_URL_64_BIT else PYTHON_PACKAGE_URL_32_BIT
    val outputFile = JFile(context.filesDir, "python.7z")

    scope.launchWithProgressDialog(
        uiContext = context,
        context = Dispatchers.IO,
        configureBuilder = {
            it.setTitle("Downloading Python")
                .setMessage(strings.python_downloading_python_compiler)
                .setCancelable(false)
                .setIndeterminate(false)
                .setMax(100)
        }
    ) { builder, dialog ->
        PRDownloader.download(url, outputFile.parent, outputFile.name)
            .build()
            .setOnProgressListener {
                val progress = (it.currentBytes * 100 / it.totalBytes).toInt()
                builder.setProgress(progress).setMessage("Downloading... $progress%")
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    extractPythonFile(
                        scope = scope,
                        context = context,
                        filePath = outputFile.absolutePath
                    ) {
                        outputFile.delete()
                        pythonDownloaded = true
                        onDownloaded()
                    }
                }

                override fun onError(error: Error) {
                    dialog.dismiss()

                    Snackbar.make(
                        view,
                        if (error.isConnectionError) context.getString(R.string.connection_failed) else if (error.isServerError) "Server error!" else "Download failed! Something went wrong.",
                        Snackbar.LENGTH_SHORT
                    ).setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show()
                }
            })
    }
}