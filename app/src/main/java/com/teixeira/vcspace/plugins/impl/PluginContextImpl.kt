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
package com.teixeira.vcspace.plugins.impl

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.itsvks.monaco.MonacoEditor
import com.teixeira.vcspace.activities.EditorActivity
import com.teixeira.vcspace.app.rootView
import com.teixeira.vcspace.core.EventManager
import com.teixeira.vcspace.core.MenuManager
import com.teixeira.vcspace.core.PanelManager
import com.teixeira.vcspace.core.components.DraggableFloatingPanel
import com.teixeira.vcspace.editor.VCSpaceEditor
import com.teixeira.vcspace.file.wrapFile
import com.teixeira.vcspace.keyboard.CommandPaletteManager
import com.teixeira.vcspace.keyboard.model.Command
import com.teixeira.vcspace.plugins.DialogManager
import com.teixeira.vcspace.preferences.defaultPrefs
import com.teixeira.vcspace.ui.screens.editor.EditorViewModel
import com.teixeira.vcspace.ui.screens.editor.components.view.CodeEditorView
import com.teixeira.vcspace.utils.runOnUiThread
import com.vcspace.plugins.Editor
import com.vcspace.plugins.PluginContext
import com.vcspace.plugins.Workspace
import com.vcspace.plugins.command.EditorCommand
import com.vcspace.plugins.dialog.DialogButtonClickListener
import com.vcspace.plugins.editor.Position
import com.vcspace.plugins.event.EventListener
import com.vcspace.plugins.event.EventType
import com.vcspace.plugins.menu.MenuItem
import com.vcspace.plugins.network.HttpResponse
import com.vcspace.plugins.panel.ComposeFactory
import com.vcspace.plugins.panel.Panel
import com.vcspace.plugins.panel.ViewFactory
import com.vcspace.plugins.panel.ViewUpdater
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.util.UUID
import kotlin.math.max

class PluginContextImpl(
    editorActivity: EditorActivity,
    editorViewModel: EditorViewModel,
    private val compositionContext: CompositionContext
) : PluginContext {
    override val appContext: Context = editorActivity
    override val editor: Editor
    override val rootView: ViewGroup
        get() = activity.rootView()

    private val activity = editorActivity
    private var editorViewModel: EditorViewModel

    init {
        this.editorViewModel = editorViewModel
        this.editor = EditorImpl(EditorListener())
    }

    override val openedFolder: File?
        get() = activity.openedFolder()?.asRawFile()

    override fun registerCommand(command: EditorCommand) {
        val commandManager = CommandPaletteManager.instance
        commandManager.addCommand(Command.newCommand(command.name, command.keyBinding) {
            command.execute(editor)
        })
    }

    override fun addMenu(menuItem: MenuItem) {
        val menuManager = MenuManager.instance
        menuManager.addMenu(
            com.teixeira.vcspace.core.menu.MenuItem(
                menuItem.title,
                menuItem.id,
                visible = true,
                enabled = true,
                shortcut = menuItem.shortcut,
                icon = null,
                trailingIcon = null,
                onClick = menuItem.action::doAction
            )
        )
    }

    override fun openFile(file: File) {
        activity.openFile(file.wrapFile())
    }

    override fun createComposePanel(
        title: String,
        content: ComposeFactory,
        dismissOnClickOutside: Boolean
    ): Panel {
        return createComposePanelInternal(title, { content.Create() }, dismissOnClickOutside)
    }

    override fun <T : View> createViewPanel(
        title: String,
        factory: ViewFactory<T>,
        update: ViewUpdater<T>,
        dismissOnClickOutside: Boolean
    ): Panel {
        return createComposePanelInternal(title, @Composable {
            AndroidView(
                factory = factory::create,
                modifier = Modifier.wrapContentSize(),
                update = update::accept
            )
        }, dismissOnClickOutside)
    }

    override fun removePanel(panelId: String): Boolean {
        val panelManager = PanelManager.instance
        val panel = panelManager.getPanelById(panelId) ?: return false
        panel.hide()

        panelManager.removePanel(id = panelId)
        return true
    }

    override fun doActionOnIOThread(action: Runnable) {
        activity.lifecycleScope.launch(Dispatchers.IO + SupervisorJob()) {
            action.run()
        }
    }

    override fun doActionOnMainThread(action: Runnable) {
        activity.lifecycleScope.launch(Dispatchers.Main.immediate + SupervisorJob()) {
            action.run()
        }
    }

    override fun registerEventListener(type: EventType, listener: EventListener) {
        val eventManager = EventManager.instance
        eventManager.registerListener(type, listener)
    }

    override fun unregisterEventListener(type: EventType, listener: EventListener) {
        val eventManager = EventManager.instance
        eventManager.unregisterListener(type, listener)
    }

    override fun saveConfig(key: String, value: String) {
        defaultPrefs.edit(commit = true) { putString(key, value) }
    }

    override fun loadConfig(key: String, defaultValue: String): String {
        return defaultPrefs.getString(key, defaultValue) ?: defaultValue
    }

    override fun showDialog(
        title: String,
        message: String,
        positiveButtonText: String,
        negativeButtonText: String?,
        onPositiveClick: DialogButtonClickListener?,
        onNegativeClick: DialogButtonClickListener?
    ) {
        val dialogManager = DialogManager.instance

        dialogManager.title.value = title
        dialogManager.message.value = message
        dialogManager.positiveButtonText.value = positiveButtonText
        dialogManager.negativeButtonText.value = negativeButtonText ?: ""
        dialogManager.positiveButtonClickListener.value = onPositiveClick
        dialogManager.negativeButtonClickListener.value = onNegativeClick

        dialogManager.showDialog()
    }

    override fun getWorkspace(): Workspace? {
        if (activity.fileExplorerViewModel.openedFolder.value == null) return null

        return WorkspaceImpl(activity.fileExplorerViewModel)
    }

    override fun registerShortcut(keyBinding: String, action: Runnable) {
        CommandPaletteManager.instance.addShortcut(keyBinding) { action.run() }
    }

    override fun httpGet(url: String): HttpResponse {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        return try {
            client.newCall(request).execute().use { response ->
                HttpResponse(
                    statusCode = response.code,
                    headers = response.headers.toMultimap(),
                    body = response.body?.string().orEmpty()
                )
            }
        } catch (e: IOException) {
            HttpResponse(
                statusCode = -1,
                headers = emptyMap(),
                body = "",
                error = e.message
            )
        }
    }

    private fun createComposePanelInternal(
        title: String,
        content: @Composable () -> Unit,
        dismissOnClickOutside: Boolean,
    ): Panel {
        val id = UUID.randomUUID().toString()
        val panel = PanelManager.instance.addPanel(id, title) { content() }

        @Composable
        fun PanelComposable() {
            val isVisible by remember { derivedStateOf { panel.isVisible } }
            var internalOffset by remember { mutableStateOf(panel.offset) }
            LaunchedEffect(panel.offset) {
                internalOffset = panel.offset
            }

            if (isVisible) {
                DraggableFloatingPanel(
                    title = title,
                    offset = internalOffset,
                    onOffsetChange = { newOffset ->
                        internalOffset = newOffset
                        panel.offset = newOffset
                    },
                    onDismiss = { panel.hide() },
                    dismissOnClickOutside = dismissOnClickOutside,
                ) {
                    content()
                }
            }
        }

        runOnUiThread {
            val composeView = ComposeView(activity).apply {
                setParentCompositionContext(compositionContext)
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
                setContent {
                    if (panel.isVisible) {
                        PanelComposable()
                    }
                }
            }
            activity.rootView().addView(composeView)
        }

        return panel
    }

    private inner class EditorListener : EditorImpl.Listener {
        override val currentFile: File?
            get() {
                val selectedFile = editorViewModel.uiState.value.selectedFile ?: return null
                return selectedFile.file.asRawFile()
            }

        override val context: Context
            get() = appContext

        override var cursorPosition: Position
            get() {
                val editor = activity.currentEditor
                if (editor is MonacoEditor) {
                    val position = editor.position
                    return Position(position.lineNumber, position.column)
                } else if (editor is CodeEditorView) {
                    val cursor = editor.editor.cursor
                    return Position(cursor.leftLine, cursor.leftColumn)
                }
                return Position()
            }
            set(position) {
                val editor = activity.currentEditor
                if (editor is MonacoEditor) {
                    editor.position =
                        com.itsvks.monaco.option.Position(position.lineNumber, position.column)
                } else if (editor is CodeEditorView) {
                    val cursor = editor.editor.cursor
                    cursor.set(max(position.lineNumber - 1, 0), max(position.column - 1, 0))
                }
            }

        override val editor: VCSpaceEditor?
            get() = (activity.currentEditor as? CodeEditorView)?.editor
    }

    fun setEditorViewModel(editorViewModel: EditorViewModel) {
        this.editorViewModel = editorViewModel
    }
}
